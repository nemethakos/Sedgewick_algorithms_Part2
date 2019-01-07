import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;

import java.util.Arrays;

public class SAP {

    private Digraph digraph;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        this.digraph = new Digraph(G);
    }

    // do unit testing of this class
    public static void main(String[] args) {
        //
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        return getLowestCommonAncestor(Arrays.asList(v), Arrays.asList(w)).distance;
    }

    private LowestCommonAncestor getLowestCommonAncestor(Iterable<Integer> v, Iterable<Integer> w) {
        checkIterableIfContainsValidVertexIds(v);
        checkIterableIfContainsValidVertexIds(w);

        BreadthFirstDirectedPaths vPath = new BreadthFirstDirectedPaths(this.digraph, v);
        BreadthFirstDirectedPaths wPath = new BreadthFirstDirectedPaths(this.digraph, w);

        int commonAncestorId = -1;
        int minDistance = Integer.MAX_VALUE;
        for (int i = 0; i < this.digraph.V(); i++) {
            if (vPath.hasPathTo(i) && wPath.hasPathTo(i)) {
                int vDistance = vPath.distTo(i);
                int wDistance = wPath.distTo(i);
                int vWDistance = vDistance + wDistance;
                if (minDistance > vWDistance) {
                    minDistance = vWDistance;
                    commonAncestorId = i;
                }
            }
        }

        int distance = commonAncestorId == -1 ? -1 : minDistance;
        return new LowestCommonAncestor(distance, commonAncestorId);
    }

    private void checkIterableIfContainsValidVertexIds(Iterable<Integer> iterable) {
        if (iterable == null) {
            throw new IllegalArgumentException("null element in Iterator!");
        }
        for (Integer i : iterable) {
            if (i == null) {
                throw new IllegalArgumentException("Element in iterator is null!");
            }
            if (i < 0 || i >= this.digraph.V()) {
                throw new IllegalArgumentException(String.format("Node index: %d out of range [0,%d]", i, this.digraph.V()));
            }
        }
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        return getLowestCommonAncestor(Arrays.asList(v), Arrays.asList(w)).commonAncestorId;
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        return getLowestCommonAncestor(v, w).distance;
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        return getLowestCommonAncestor(v, w).commonAncestorId;
    }

    private static class LowestCommonAncestor {
        private int distance;
        private int commonAncestorId;

        public LowestCommonAncestor(int distance, int commonAncestorId) {
            this.distance = distance;
            this.commonAncestorId = commonAncestorId;
        }
    }
}
