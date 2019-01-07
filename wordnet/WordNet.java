import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;

import java.util.HashMap;
import java.util.Map;

public class WordNet {

    private SynsetResult synsetResult;
    private Digraph digraph;
    private SAP sap;

    /**
     * Constructor takes the name of the two input files
     */
    public WordNet(String synsetsFileName, String hypernymsFileName) {

        nullCheck(synsetsFileName);
        nullCheck(hypernymsFileName);

        this.synsetResult = readSynsets(synsetsFileName);
        this.digraph = getDigraphFromHypernymsFile(hypernymsFileName, this.synsetResult.vertextIdToSynsetMap.size());
        this.sap = new SAP(digraph);

        checkNoCyclesExistsInDigraph(synsetsFileName, hypernymsFileName);
        checkDigraphHasOnlyOneRoot(synsetsFileName, hypernymsFileName);
    }

    private static void nullCheck(Object arg) {
        if (arg == null) {
            throw new IllegalArgumentException("Null object!");
        }
    }

    private SynsetResult readSynsets(String synsetsFileName) {

        Map<String, Bag<Integer>> nounToSynsetMap = new HashMap<>();
        Map<Integer, String> vertexIdToNounMap = new HashMap<>();
        Map<Integer, String> vertexIdToSynsetMap = new HashMap<>();

        In in = new In(synsetsFileName);

        while (in.hasNextLine()) {
            String[] elements = in.readLine().split(",");
            int vertexId = Integer.parseInt(elements[0]);
            String synset = elements[1];

            vertexIdToSynsetMap.put(vertexId, synset);

            String[] nouns = synset.split(" ");

            for (String noun : nouns) {
                Bag<Integer> collectionOfSynsetIds = nounToSynsetMap.get(noun);
                if (collectionOfSynsetIds == null) {
                    collectionOfSynsetIds = new Bag<>();
                    nounToSynsetMap.put(noun, collectionOfSynsetIds);
                }
                collectionOfSynsetIds.add(vertexId);
                vertexIdToNounMap.put(vertexId, noun);
            }
        }
        in.close();

        return new SynsetResult(nounToSynsetMap, vertexIdToNounMap, vertexIdToSynsetMap);
    }

    private Digraph getDigraphFromHypernymsFile(String hypernymsFileName, int size) {

        Digraph result = new Digraph(size);

        In in = new In(hypernymsFileName);

        while (in.hasNextLine()) {
            String[] elements = in.readLine().split(",");

            int vertexId = Integer.parseInt(elements[0]);
            for (int i = 1; i < elements.length; i++) {
                result.addEdge(vertexId, Integer.parseInt(elements[i]));
            }
        }

        return result;
    }

    private void checkNoCyclesExistsInDigraph(String synsetsFileName, String hypernymsFileName) {
        DirectedCycle dc = new DirectedCycle(this.digraph);
        if (dc.hasCycle()) {
            throw new IllegalArgumentException(String.format("Digraph is not a DAG! synsets: %s, hypernyms: %s", synsetsFileName, hypernymsFileName));
        }
    }

    private void checkDigraphHasOnlyOneRoot(String synsetsFileName, String hypernymsFileName) {
        int rootCount = 0;
        for (int i = 0; i < this.digraph.V(); i++) {

            if (!this.digraph.adj(i).iterator().hasNext()) {
                rootCount++;
            }
        }

        if (rootCount != 1) {
            throw new IllegalArgumentException(String.format("Digraph's root count is not 1! root count: %d synsets: %s, hypernyms: %s", rootCount, synsetsFileName, hypernymsFileName));
        }
    }

    /**
     * Do unit testing of this class
     */
    public static void main(String[] args) {
        //
    }

    /**
     * Returns all WordNet nouns
     */
    public Iterable<String> nouns() {
        return this.synsetResult.nounToSynsetMap.keySet();
    }

    /**
     * Is the word a WordNet noun?
     *
     * @param word
     * @return
     */
    public boolean isNoun(String word) {
        nullCheck(word);
        return this.synsetResult.nounToSynsetMap.get(word) != null;
    }

    /**
     * Distance between nounA and nounB (defined below)
     */
    public int distance(String nounA, String nounB) {
        return sap.length(findSynsetContainingTheNoun(nounA), findSynsetContainingTheNoun(nounB));
    }

    private Bag<Integer> findSynsetContainingTheNoun(String noun) {
        nullCheck(noun);
        Bag<Integer> synsetIds = this.synsetResult.nounToSynsetMap.get(noun);
        if (synsetIds == null) {
            throw new IllegalArgumentException("noun not found: '" + noun + "'");
        }
        return synsetIds;
    }

    /**
     * A synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
     * in a shortest ancestral path (defined below)
     */
    public String sap(String nounA, String nounB) {
        return this.synsetResult.vertextIdToSynsetMap.get(sap.ancestor(findSynsetContainingTheNoun(nounA), findSynsetContainingTheNoun(nounB)));
    }

    private static class SynsetResult {
        private Map<String, Bag<Integer>> nounToSynsetMap;
        private Map<Integer, String> vertexIdToNounMap;
        private Map<Integer, String> vertextIdToSynsetMap;

        public SynsetResult(
                Map<String, Bag<Integer>> nounToSynsetMap,
                Map<Integer, String> vertexIdToNounMap,
                Map<Integer, String> vertextIdToSynsetMap) {

            this.nounToSynsetMap = nounToSynsetMap;
            this.vertexIdToNounMap = vertexIdToNounMap;
            this.vertextIdToSynsetMap = vertextIdToSynsetMap;
        }
    }

}