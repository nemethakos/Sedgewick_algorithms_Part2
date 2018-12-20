import edu.princeton.cs.algs4.Graph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.Deque;
import java.util.LinkedList;

/**
 * Contains both recursive and iterative DFS for comparison
 * for testing:
 * java ... IterativeDFS <graph definition file name> <start vertex>
 * e.g.: java ... IterativeDFS largeG.txt 0
 */
public class IterativeDFS {
    private boolean[] marked;    // marked[v] = is there an s-v path?
    private int sourceVertexCount;           // number of vertices connected to s
    private Deque<Integer> stack;

    /**
     * Computes the vertices in graph {@code graph} that are
     * connected to the source vertex {@code sourceVertex}.
     *
     * @param graph        the graph
     * @param sourceVertex the source vertex
     * @throws IllegalArgumentException unless {@code 0 <= sourceVertex < V}
     */
    public IterativeDFS(Graph graph, int sourceVertex, boolean recursiveDFS) {
        marked = new boolean[graph.V()];
        stack = new LinkedList<>();
        validateVertex(sourceVertex);
        if (recursiveDFS) {
            recursiveDfs(graph, sourceVertex);
        } else {
            iterativeDfs(graph, sourceVertex);
        }
    }

    // depth first search from sourceVertex
    public void iterativeDfs(Graph graph, int sourceVertex) {

        stack.addLast(sourceVertex);

        while (!stack.isEmpty()) {
            int vertex = stack.removeLast();

            if (!marked[vertex]) {
                sourceVertexCount++;
                marked[vertex] = true;

                for (int adjacentVertex : graph.adj(vertex)) {
                    if (!marked[adjacentVertex]) {
                        stack.addLast(adjacentVertex);
                    }
                }
            }
        }
    }

    // depth first search from v
    public void recursiveDfs(Graph G, int v) {
        sourceVertexCount++;
        marked[v] = true;
        for (int w : G.adj(v)) {
            if (!marked[w]) {
                recursiveDfs(G, w);
            }
        }
    }

    /**
     * Is there a path between the source vertex {@code s} and vertex {@code vertex}?
     *
     * @param vertex the vertex
     * @return {@code true} if there is a path, {@code false} otherwise
     * @throws IllegalArgumentException unless {@code 0 <= vertex < V}
     */
    public boolean marked(int vertex) {
        validateVertex(vertex);
        return marked[vertex];
    }

    /**
     * Returns the number of vertices connected to the source vertex {@code s}.
     *
     * @return the number of vertices connected to the source vertex {@code s}
     */
    public int count() {
        return sourceVertexCount;
    }

    // throw an IllegalArgumentException unless {@code 0 <= vertexNumber < V}
    private void validateVertex(int vertexNumber) {
        int numberOfVertexes = marked.length;
        if (vertexNumber < 0 || vertexNumber >= numberOfVertexes) {
            throw new IllegalArgumentException("vertexNumber " + vertexNumber + " is not between 0 and " + (numberOfVertexes - 1));
        }
    }

    /**
     * Unit tests the {@code DepthFirstSearch} data type.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        System.out.println("Recursive:");
        testConnected(args[0], args[1], true);
        System.out.println("---------------------------------------------------------------------------");
        System.out.println("Iterative:");
        testConnected(args[0], args[1], false);
    }

    private static void testConnected(String fileName, String sourceVertexStr, boolean recursiveDFS) {
        try {

            In in = new In(fileName);
            Graph graph = new Graph(in);
            int sourceVertex = Integer.parseInt(sourceVertexStr);
            System.out.println("Source vertex: " + sourceVertexStr);
            IterativeDFS search = new IterativeDFS(graph, sourceVertex, recursiveDFS);
            for (int v = 0; v < graph.V(); v++) {
                if (search.marked(v))
                    StdOut.print(v + " ");
            }

            StdOut.println("\r\ncount: " + search.count() + ", #vertices: " + graph.V());
            if (search.count() != graph.V()) StdOut.println("NOT connected");
            else StdOut.println("connected");
        } catch (Error e) {
            System.out.println("Error: " + e);
            e.printStackTrace();
        }
    }

}

