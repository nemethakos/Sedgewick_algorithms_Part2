/**
 * Modified version of {@link edu.princeton.cs.algs4.TST}. It exposes the {@link Node}s internals
 *
 * The {@code TST} class represents an symbol table of key-value pairs, with string keys and generic
 * values. It supports the usual <em>put</em>, <em>get</em>, <em>contains</em>,
 * <em>delete</em>, <em>size</em>, and <em>is-empty</em> methods.
 * It also provides character-based methods for finding the string in the symbol table that is the
 * <em>longest prefix</em> of a given prefix, finding all strings in the symbol table that
 * <em>start
 * with</em> a given prefix, and finding all strings in the symbol table that <em>match</em> a given
 * pattern. A symbol table implements the <em>associative array</em> abstraction: when associating a
 * value with a key that is already in the symbol table, the convention is to replace the old value
 * with the new value. Unlike {@link java.util.Map}, this class uses the convention that values
 * cannot be {@code null}—setting the value associated with a key to {@code null} is equivalent to
 * deleting the key from the symbol table.
 * <p>
 * This implementation uses a ternary search trie.
 * <p>
 * For additional documentation, see <a href="https://algs4.cs.princeton.edu/52trie">Section 5.2</a>
 * of
 * <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 */
public class BoggleTST<Value> {
    private int n;              // size
    private Node<Value> root;   // root of TST

    /**
     * Initializes an empty string symbol table.
     */
    public BoggleTST() {
    }

    /**
     * Returns the number of key-value pairs in this symbol table.
     *
     * @return the number of key-value pairs in this symbol table
     */
    public int size() {
        return n;
    }

    public Node<Value> getRoot() {
        return root;
    }

    /**
     * Does this symbol table contain the given key?
     *
     * @param key the key
     * @return {@code true} if this symbol table contains {@code key} and {@code false} otherwise
     * @throws IllegalArgumentException if {@code key} is {@code null}
     */
  public boolean contains(String key) {
        if (key == null) {
            throw new IllegalArgumentException("argument to contains() is null");
        }
        return get(key) != null;
    }

    /**
     * Returns the value associated with the given key.
     *
     * @param key the key
     * @return the value associated with the given key if the key is in the symbol table and {@code
     * null} if the key is not in the symbol table
     * @throws IllegalArgumentException if {@code key} is {@code null}
     */
    public Value get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("calls get() with null argument");
        }
        if (key.length() == 0) throw new IllegalArgumentException("key must have length >= 1");
        Node<Value> x = get(root, key, 0);
        if (x == null) return null;
        return x.val;
    }

    // return subtrie corresponding to given key
    public Node<Value> get(Node<Value> x, String key, int d) {
        // System.out.format("get %s, key: %s, d: %d\n", x, key, d);
        if (x == null) return null;
        if (key.length() == 0) throw new IllegalArgumentException("key must have length >= 1");
        char c = key.charAt(d);
        if (c < x.c) return get(x.left, key, d);
        else if (c > x.c) return get(x.right, key, d);
        else if (d < key.length() - 1) return get(x.mid, key, d + 1);
        else return x;
    }

    // return subtrie corresponding to given key
    public Node<Value> get(Node<Value> x, char[] key, int keyLength, int d) {
        if (x == null) return null;
        if (keyLength == 0) throw new IllegalArgumentException("key must have length >= 1");
        char c = key[d];
        if (c < x.c) return get(x.left, key, keyLength, d);
        else if (c > x.c) return get(x.right, key, keyLength, d);
        else if (d < keyLength - 1) return get(x.mid, key, keyLength, d + 1);
        else return x;
    }

    /**
     * Inserts the key-value pair into the symbol table, overwriting the old value with the new
     * value if the key is already in the symbol table. If the value is {@code null}, this
     * effectively deletes the key from the symbol table.
     *
     * @param key the key
     * @param val the value
     * @throws IllegalArgumentException if {@code key} is {@code null}
     */
    public void put(String key, Value val) {
        if (key == null) {
            throw new IllegalArgumentException("calls put() with null key");
        }
        if (!contains(key)) n++;
        root = put(root, key, val, 0);
    }

    private Node<Value> put(Node<Value> x, String key, Value val, int d) {
        char c = key.charAt(d);
        if (x == null) {
            x = new Node<Value>();
            x.c = c;
        }
        if (c < x.c) x.left = put(x.left, key, val, d);
        else if (c > x.c) x.right = put(x.right, key, val, d);
        else if (d < key.length() - 1) x.mid = put(x.mid, key, val, d + 1);
        else x.val = val;
        return x;
    }

    public static class Node<Value> {

        private char c;                        // character
        private Node<Value> left, mid, right;  // left, middle, and right subtries
        private Value val;                     // value associated with string

        public Node<Value> getMid() {
            return mid;
        }

        public Value getVal() {
            return val;
        }
    }
}

/******************************************************************************
 *  Copyright 2002-2018, Robert Sedgewick and Kevin Wayne.
 *
 *  This file is part of algs4.jar, which accompanies the textbook
 *
 *      Algorithms, 4th edition by Robert Sedgewick and Kevin Wayne,
 *      Addison-Wesley Professional, 2011, ISBN 0-321-57351-X.
 *      http://algs4.cs.princeton.edu
 *
 *
 *  algs4.jar is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  algs4.jar is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with algs4.jar.  If not, see http://www.gnu.org/licenses.
 ******************************************************************************/
