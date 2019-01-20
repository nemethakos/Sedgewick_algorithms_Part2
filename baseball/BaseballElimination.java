import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseballElimination {

    private Team[] teams;
    private List<String> teamNameList;
    private Map<String, Team> teamNameToTeamMap;
    private Map<String, List<String>> teamNameToEliminatorMap;

    /**
     * Create a baseball division from given filename in format specified below
     * <p>
     * The input format is the number of teams in the division n followed by one line for each team.
     * Each line contains the team name (with no internal whitespace characters), the number of
     * wins, the number of losses, the number of remaining games, and the number of remaining games
     * against each team in the division.
     * <pre>
     * % more teams4.txt
     * 4
     * Atlanta       83 71  8  0 1 6 1
     * Philadelphia  80 79  3  1 0 0 2
     * New_York      78 78  6  6 0 0 0
     * Montreal      77 82  3  1 2 0 0
     *
     * % more teams5.txt
     * 5
     * New_York    75 59 28   0 3 8 7 3
     * Baltimore   71 63 28   3 0 2 7 7
     * Boston      69 66 27   8 2 0 0 3
     * Toronto     63 72 27   7 7 0 0 3
     * Detroit     49 86 27   3 7 3 3 0
     * </pre>
     * <p>You may assume that n ≥ 1 and that the input files are in the specified format and
     * internally consistent. Note that a team's number of remaining games does not necessarily
     * equal the sum of the remaining games against teams in its division because a team may play
     * opponents outside its division.</p>
     *
     * @param filename the file containing the input
     */
    public BaseballElimination(String filename) {
        In in = new In(filename);
        int numTeams = Integer.parseInt(in.readLine());

        this.teamNameToTeamMap = new HashMap<>();
        this.teamNameList = new ArrayList<>();
        this.teams = new Team[numTeams];
        int i = 0;
        int id = 0;
        while (in.hasNextLine()) {
            teams[id] = new Team(in, id, numTeams);
            teamNameList.add(teams[id].name);
            teamNameToTeamMap.put(teams[id].name, teams[id]);
            id++;
        }
        in.close();
        teamNameToEliminatorMap = processTeams();
    }

    /**
     * Returns the map of elimination
     *
     * @return the map of elimination
     */
    private Map<String, List<String>> processTeams() {
        List<String> eliminatorList;
        Map<String, List<String>> result = new HashMap<>();

        for (int teamIdToExclude = 0; teamIdToExclude < teams.length; teamIdToExclude++) {

            String currentTeamName = teams[teamIdToExclude].name;

            FlowNetworkVertices flowNetworkVertices = getFlowNetworkVertices(teamIdToExclude);

            eliminatorList = flowNetworkVertices.getEliminatorList();

            if (eliminatorList.size() > 0) {
                result.put(currentTeamName, eliminatorList);
            }
            else {

                FlowNetwork fn = getFlowNetwork(flowNetworkVertices);

                FordFulkerson ff = new FordFulkerson(fn, flowNetworkVertices.start.id,
                                                     flowNetworkVertices.terminal.id);

                if (ff.value() < flowNetworkVertices.getSaturatedFlowAmount()) {

                    eliminatorList = new ArrayList<>();
                    for (FlowNetworkVertex fnv : flowNetworkVertices.teamVertices) {
                        if (ff.inCut(fnv.id)) {
                            int team = flowNetworkVertices.idToFlownetworkVertex.get(fnv.id).team;
                            String name = teams[team].name;
                            eliminatorList.add(name);
                        }
                    }
                    result.put(currentTeamName, eliminatorList);

                }

            }
        }
        return result;
    }

    /**
     * Returns the {@link FlowNetworkVertices} for a flow network where the team identified by
     * <code>teamIdToExclude</code> is left out
     *
     * @param teamIdToExclude the team id which is left out from the flow network
     * @return the {@link FlowNetworkVertices}
     */
    private FlowNetworkVertices getFlowNetworkVertices(int teamIdToExclude) {

        FlowNetworkVertex.idGenerator = 0;

        FlowNetworkVertices result = new FlowNetworkVertices(FlowNetworkVertex.getStartVertex(),
                                                             FlowNetworkVertex.getTerminalVertex(),
                                                             getGameVertices(teamIdToExclude),
                                                             getTeamVertices(teamIdToExclude));

        return result;
    }

    /**
     * Returns a {@link FlowNetwork} containing all the teams except <code>id</code>th team
     *
     * @return the {@link FlowNetwork}
     */
    private FlowNetwork getFlowNetwork(FlowNetworkVertices vertices) {
        FlowEdge fe;
        FlowNetwork fn = new FlowNetwork(vertices.getNumberOfVertices());

        for (FlowNetworkVertex game : vertices.gameVertices) {
            // start -> game vertices
            fn.addEdge(new FlowEdge(vertices.start.id, game.id, game.gamesLeftBetweenTeams));

            // FlowEdges: game vertices -> team vertices
            int team1 = game.gameTeam1;
            int team2 = game.gameTeam2;

            int team1Id = vertices.teamIdToFlownetworkVertex.get(team1).id;
            fn.addEdge(new FlowEdge(game.id, team1Id, Double.POSITIVE_INFINITY));

            int team2Id = vertices.teamIdToFlownetworkVertex.get(team2).id;
            fn.addEdge(new FlowEdge(game.id, team2Id, Double.POSITIVE_INFINITY));
        }

        for (FlowNetworkVertex team : vertices.teamVertices) {
            fn.addEdge(new FlowEdge(team.id, vertices.terminal.id, team.teamWinsAchieable));
        }

        return fn;
    }

    /**
     * Returns the {@link List} of {@link FlowNetworkVertex} for a flow network where the team
     * <code>teamidToExclude</code> is left out
     *
     * @param teamIdToExclude the id of the team which is left out
     * @return the {@link List} of {@link FlowNetworkVertex}
     */
    private List<FlowNetworkVertex> getGameVertices(int teamIdToExclude) {
        List<FlowNetworkVertex> result = new ArrayList<>();

        for (int team1 = 0; team1 < teams.length; team1++) {
            for (int team2 = team1 + 1; team2 < teams.length; team2++) {
                if (team1 != teamIdToExclude && team2 != teamIdToExclude
                        && getGamesLeftBetweenTeams(team1, team2) > 0) {

                    result.add(FlowNetworkVertex.getGameVertex(team1, team2,
                                                               getGamesLeftBetweenTeams(team1,
                                                                                        team2)));

                }
            }
        }
        return result;
    }

    /**
     * Returns the {@link List} of {@link FlowNetworkVertex} for a flow network where the team *
     * <code>teamidToExclude</code> is left out
     *
     * @param teamIdToExclude the id of the team which is left out
     * @return the {@link List} of {@link FlowNetworkVertex}
     */
    private List<FlowNetworkVertex> getTeamVertices(int teamIdToExclude) {
        List<FlowNetworkVertex> result = new ArrayList<>();
        for (int team = 0; team < teams.length; team++) {
            if (team != teamIdToExclude) {
                result.add(
                        FlowNetworkVertex
                                .getTeamVertex(team, getWinsAchieavableByTeam(teamIdToExclude,
                                                                              team),
                                               teams[team].name));
            }
        }
        return result;
    }

    private int getGamesLeftBetweenTeams(int team1, int team2) {
        return teams[team1].gamesLeft[team2];
    }

    private int getWinsAchieavableByTeam(int team, int otherTeam) {
        return teams[team].wonGames + teams[team].remainingGames - teams[otherTeam].wonGames;
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }

    /**
     * Returns all team names
     *
     * @return list of team names
     */
    public Iterable<String> teams() {
        return teamNameList;
    }

    /**
     * is given team eliminated?
     * <p>
     * Should throw a java.lang.IllegalArgumentException if one (or both) of the input arguments are
     * invalid teams.
     *
     * @param team the name of the team
     * @return true if team is eliminated
     */
    public boolean isEliminated(String team) {
        checkTeamName(team);

        return teamNameToEliminatorMap.containsKey(team);
    }

    /**
     * subset R of teams that eliminates given team; null if not eliminated
     * <p>
     * Should throw a java.lang.IllegalArgumentException if one (or both) of the input arguments are
     * invalid teams.
     *
     * @param team the name of the team
     * @return List of other teams which are eliminating the team
     */
    public Iterable<String> certificateOfElimination(String team) {
        checkTeamName(team);
        return teamNameToEliminatorMap.get(team);
    }

    private void checkTeamName(String team) {
        if (team == null || !teamNameToTeamMap.containsKey(team)) {
            throw new IllegalArgumentException("Invalid team name: " + team);
        }
    }

    @Override
    public String toString() {
        return "BaseballElimination{" +
                "teams=\n" + Arrays.toString(teams) +
                '}';
    }

    /**
     * returns the number of teams
     *
     * @return the number of teams
     */
    public int numberOfTeams() {
        return teams.length;
    }

    /**
     * number of wins for given team
     * <p>
     * Should throw a java.lang.IllegalArgumentException if one (or both) of the input arguments are
     * invalid teams.
     *
     * @param team the name of the team
     * @return the wins for the team from the input file
     */
    public int wins(String team) {
        checkTeamName(team);
        return teamNameToTeamMap.get(team).wonGames;
    }

    /**
     * number of losses for given team
     * <p>
     * Should throw a java.lang.IllegalArgumentException if one (or both) of the input arguments are
     * invalid teams.
     *
     * @param team the name of the team
     * @return number of losses for the team from the input file
     */
    public int losses(String team) {
        checkTeamName(team);
        return teamNameToTeamMap.get(team).lostGames;
    }

    /**
     * number of remaining games for given team
     * <p>
     * Should throw a java.lang.IllegalArgumentException if one (or both) of the input arguments are
     * invalid teams.
     *
     * @param team the name of the team
     * @return the remaining matches for the team from the input file
     */
    public int remaining(String team) {
        checkTeamName(team);
        return teamNameToTeamMap.get(team).remainingGames;
    }

    /**
     * number of remaining games between team1 and team2
     * <p>
     * Should throw a java.lang.IllegalArgumentException if one (or both) of the input arguments are
     * invalid teams.
     *
     * @param team1 the name of the first team
     * @param team2 the name of the second team
     * @return number of remaining games between team1 and team2
     */
    public int against(String team1, String team2) {
        checkTeamName(team1);
        checkTeamName(team2);

        int team2Id = teamNameToTeamMap.get(team2).id;
        return teamNameToTeamMap.get(team1).gamesLeft[team2Id];
    }

    /**
     * Type information for the FlowNetwork vertices
     */
    private static enum FlowNetworkWertexType {
        /**
         * Start vertex
         */
        START,
        /**
         * Terminal vertex
         */
        TERMINAL,
        /**
         * Game vertices
         */
        GAME,
        /**
         * Team vertices
         */
        TEAM
    }

    /**
     * Class for containing all information for the construction of the {@link FlowNetwork}
     */
    private static class FlowNetworkVertices {

        private Map<Integer, FlowNetworkVertex> idToFlownetworkVertex = new HashMap<>();
        private Map<Integer, FlowNetworkVertex> teamIdToFlownetworkVertex = new HashMap<>();

        private FlowNetworkVertex start;
        private FlowNetworkVertex terminal;
        private List<FlowNetworkVertex> gameVertices;
        private List<FlowNetworkVertex> teamVertices;

        public FlowNetworkVertices(FlowNetworkVertex start, FlowNetworkVertex terminal,
                                   List<FlowNetworkVertex> gameVertices,
                                   List<FlowNetworkVertex> teamVertices) {
            this.start = start;
            this.terminal = terminal;
            this.gameVertices = gameVertices;
            this.teamVertices = teamVertices;

            idToFlownetworkVertex.put(start.id, start);
            idToFlownetworkVertex.put(terminal.id, terminal);
            gameVertices.forEach(gv -> idToFlownetworkVertex.put(gv.id, gv));
            teamVertices.forEach(tv -> idToFlownetworkVertex.put(tv.id, tv));
            teamVertices.forEach(tv -> teamIdToFlownetworkVertex.put(tv.team, tv));
        }

        @Override
        public String toString() {
            return "FlowNetworkVertices{" +
                    "\nstart=" + start +
                    "\n, terminal=" + terminal +
                    "\n, gameVertices=" + gameVertices +
                    "\n, teamVertices=" + teamVertices +
                    '}';
        }

        /**
         * Returns Number of vertices for the {@link FlowNetwork}
         *
         * @return the number of vertices for the {@link FlowNetwork}
         */
        public int getNumberOfVertices() {
            return 2 + gameVertices.size() + teamVertices.size();
        }

        /**
         * Returns the maximum amount of flow for the {@link FlowNetwork}. If the {@link
         * FlowNetwork} maximum flow reaches this amount, the team (which is left out from the flow
         * network) is not eliminated.
         *
         * @return the maximum amount of flow for the {@link FlowNetwork}
         */
        public int getSaturatedFlowAmount() {

            return gameVertices.stream().mapToInt(v -> v.gamesLeftBetweenTeams).sum();
        }

        /**
         * Returns the list of eliminators for the {@link FlowNetwork} for the simple case (number
         * of wins plus remaining games is less than the wins of the team which is left out from the
         * flow network)
         *
         * @return the {@link List} of names of the teams eliminated the team which was left out
         * from the flow network.
         */
        public List<String> getEliminatorList() {
            List<String> result = new ArrayList<>();
            for (FlowNetworkVertex v : teamVertices) {
                if (v.teamWinsAchieable < 0) {
                    result.add(v.teamName);
                }
            }
            return result;
        }
    }

    /**
     * Class containing all information for a flow network vertex
     */
    private static class FlowNetworkVertex {

        private static int idGenerator = 0;

        private int id;
        private FlowNetworkWertexType type;
        private int gameTeam1;
        private int gameTeam2;
        private int gamesLeftBetweenTeams;
        private int team;
        private int teamWinsAchieable;
        private String teamName;


        private FlowNetworkVertex(int id, FlowNetworkWertexType type, int gameTeam1, int gameTeam2,
                                  int gamesLeftBetweenTeams,
                                  int team, int teamWinsAchieable, String teamName) {
            this.id = id;
            this.type = type;
            this.gameTeam1 = gameTeam1;
            this.gameTeam2 = gameTeam2;
            this.gamesLeftBetweenTeams = gamesLeftBetweenTeams;
            this.team = team;
            this.teamWinsAchieable = teamWinsAchieable;
            this.teamName = teamName;
        }

        /**
         * Returns the start vertex
         *
         * @return the start vertex
         */
        public static FlowNetworkVertex getStartVertex() {
            return new FlowNetworkVertex(idGenerator++, FlowNetworkWertexType.START,
                                         -1, -1, -1, -1, -1, null);
        }

        /**
         * Returns the terminal vertex
         *
         * @return the terminal vertex
         */
        public static FlowNetworkVertex getTerminalVertex() {
            return new FlowNetworkVertex(idGenerator++,
                                         FlowNetworkWertexType.TERMINAL, -1, -1, -1, -1, -1, null);
        }

        /**
         * Returns a game vertex
         *
         * @param team1                 first team
         * @param team2                 second team
         * @param gamesLeftBetweenTeams the number of games left between the two teams
         * @return a game vertex
         */
        public static FlowNetworkVertex getGameVertex(int team1, int team2,
                                                      int gamesLeftBetweenTeams) {
            return new FlowNetworkVertex(idGenerator++,
                                         FlowNetworkWertexType.GAME, team1, team2,
                                         gamesLeftBetweenTeams, -1, -1, null);
        }

        /**
         * Returns a team vertex
         *
         * @param team              team index
         * @param teamWinsAchieable number of wins achievable for the team
         * @param teamName          the name of the team
         * @return a team vertex
         */
        public static FlowNetworkVertex getTeamVertex(int team, int teamWinsAchieable,
                                                      String teamName) {
            return new FlowNetworkVertex(idGenerator++,
                                         FlowNetworkWertexType.TEAM, -1, -1, -1, team,
                                         teamWinsAchieable, teamName);
        }

        @Override
        public String toString() {

            String result = id + ":" + type.toString();
            if (type == FlowNetworkWertexType.TEAM) {
                result += "(" + team + ":" + teamWinsAchieable + ")";
            }
            else if (type == FlowNetworkWertexType.GAME) {
                result += "(" + gameTeam1 + "-" + gameTeam2 + ":" + gamesLeftBetweenTeams + ")";
            }

            return result;
        }
    }

    /**
     * Class for holding information about a team read from the input file
     */
    private static class Team {
        private int id;
        private String name;
        private int wonGames;
        private int lostGames;
        private int remainingGames;
        private int[] gamesLeft;

        /**
         * name, wins, losses, to play,  <> New_York    75 59 28   0 3 8 7 3
         *
         * @param in
         */
        public Team(In in, int id, int numTeams) {
            this.id = id;
            String line = in.readLine();
            String[] parts = line.trim().split("( )+");

            this.name = parts[0];
            this.wonGames = Integer.parseInt(parts[1]);
            this.lostGames = Integer.parseInt(parts[2]);
            this.remainingGames = Integer.parseInt(parts[3]);
            gamesLeft = new int[numTeams];
            for (int i = 0; i < numTeams; i++) {
                gamesLeft[i] = Integer.parseInt(parts[i + 4]);
            }
        }

        @Override
        public String toString() {
            return "Team{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", wonGames=" + wonGames +
                    ", lostGames=" + lostGames +
                    ", remainingGames=" + remainingGames +
                    ", gamesLeft=" + Arrays.toString(gamesLeft) +
                    "}\n";
        }
    }
}
