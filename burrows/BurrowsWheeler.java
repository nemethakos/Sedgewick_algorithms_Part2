import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

import java.util.Arrays;

public class BurrowsWheeler {

    private static final int MAX_BYTE_VALUE = 255;

    // private static final short BITS_PER_BYTE = 8;
    // apply Burrows-Wheeler transform, reading from standard input and writing to standard output

    // if args[0] is '-', apply Burrows-Wheeler transform
    // if args[0] is '+', apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {

        //BurrowsWheeler.transform();



        //char[] chars = "ABRACADABRA!".toCharArray();

        //System.out.println(Arrays.toString(chars));


        //System.out.println(sort(chars));


        //String encoded = "ARD!RCAAAABB";
        // SortResult sr = sort(encoded.toCharArray());
        //int[] next = getNext(encoded.toCharArray(), sr.sorted, sr.freq, 3);

        //System.out.println(Arrays.toString(next));

        //System.out.println(Arrays.toString(decode(encoded.toCharArray(), 3)));

        /*
        String s = "ABRACADABRA!";

        CircularSuffixArray csa = new CircularSuffixArray(s);

        int length = csa.length();

        int[] index = new int[length];

        for (int i = 0; i < length; i++) {
            index[i] = csa.index(i);
        }

        for (int i=0; i<s.length(); i++) {
            System.out.println("i:"+i+", "+getLastCharOfStringAtIndex(s, index[i]));
        }*/


        if (args.length == 0) {
            throw new IllegalArgumentException("No flag (- or +) specified!");
        }
        String flag = args[0];
        switch (flag) {
            case "-":
                transform();
                break;
            case "+":
                inverseTransform();
                break;
            default:
                throw new IllegalArgumentException("Only + or - is allowed!");

        }

    }

    /**
     * Burrows–Wheeler transform. The Burrows–Wheeler transform of a string s of length n is defined
     * as follows: Consider the result of sorting the n circular suffixes of s. The Burrows–Wheeler
     * transform is the last column in the sorted suffixes array t[], preceded by the row number
     * first in which the original string ends up.
     */
    public static void transform() {
/*        DynArray da = new DynArray(16);

        while (!BinaryStdIn.isEmpty()) {
            da.add(BinaryStdIn.readChar());
        }

        String s = da.getAsString(); */
        String s = BinaryStdIn.readString();
        CircularSuffixArray csa = new CircularSuffixArray(s);

        int length = csa.length();

        int[] index = new int[length];

        int first = -1;
        for (int i = 0; i < length; i++) {
            int currentIndex = csa.index(i);
            index[i] = currentIndex;
            if (currentIndex == 0) {
                first = i;
            }
        }

        BinaryStdOut.write(first);

        for (int i = 0; i < s.length(); i++) {
            // System.out.println("i:" + i + ", " + getLastCharOfStringAtIndex(s, index[i]));
            BinaryStdOut.write((byte) (getLastCharOfStringAtIndex(s, index[i]) & MAX_BYTE_VALUE));
        }

        BinaryStdOut.close();
    }

    // apply Burrows-Wheeler inverse transform, reading from standard input and writing to standard output
    public static void inverseTransform() {
       // DynArray da = new DynArray(16);

        int first = BinaryStdIn.readInt();
/*
        while (!BinaryStdIn.isEmpty()) {
            da.add(BinaryStdIn.readChar());
        }

        // the t array
        char[] t = da.getAsCharArray();

        da = null; */

        char[] t = BinaryStdIn.readString().toCharArray();

        // the firsts array contains the characters of t in sorted order
        char[] plain = decode(t, first);

        // System.out.println(Arrays.toString(plain));

        for (int i = 0; i < plain.length; i++) {
            BinaryStdOut.write((byte) (plain[i] & MAX_BYTE_VALUE));
        }

        BinaryStdOut.close();

    }

    private static char getLastCharOfStringAtIndex(String s, int index) {
        return Util.getIthStringsDthChar(s, index + s.length() - 1);
    }

    private static char[] decode(char[] t, int first) {

        //System.out.println("input:"+Arrays.toString(t)+", first:"+first);

        SortResult sr = sort(t);

        int[] next = getNext(t, sr.sorted, sr.freq, first);

        char[] output = new char[t.length];
        int current = first;
        for (int i = 0; i < output.length; i++) {
            output[i] = sr.sorted[current];
            current = next[current];
        }

        //System.out.println("output:"+Arrays.toString(output));

        return output;
    }

    private static SortResult sort(char[] a) {
        int n = a.length;
        int R = 256;   // extend ASCII alphabet size
        char[] aux = new char[n];
        int[] freq = new int[256];

        // compute frequency counts
        int[] count = new int[R + 1];
        for (int i = 0; i < n; i++) {
            count[a[i] + 1]++;
            freq[a[i]]++;
        }

        // compute cumulates
        for (int r = 0; r < R; r++)
            count[r + 1] += count[r];

        // move data
        for (int i = 0; i < n; i++)
            aux[count[a[i]]++] = a[i];

        return new SortResult(aux, freq);

    }

    private static int[] getNext(char[] t, char[] firsts, int[] freq, int first) {
        int[] next = new int[t.length];


        int[][] loc = new int[256][0];
        int[] used = new int[256];
        for (int i = 0; i < freq.length; i++) {
            int frequencyOfCharI = freq[i];
            if (frequencyOfCharI > 0) {

                loc[i] = new int[frequencyOfCharI];
            }
        }
        for (int i = 0; i < t.length; i++) {
            char c = t[i];
            int usedCharCount = used[c]++;
            loc[c][usedCharCount] = i;
        }

        Arrays.fill(used, 0);

        for (int i = 0; i < firsts.length; i++) {
            char c = firsts[i];
            int firstPos = loc[c][used[c]++];
            next[i] = firstPos;
        }

        /*
        for (int i = 0; i < loc.length; i++) {
            if (loc[i].length > 0) {
                System.out.format("\n%c:%s", i, Arrays.toString(loc[i]));
            }
        }
        */


        return next;
    }

    private static class SortResult {
        char[] sorted;
        int[] freq;

        public SortResult(char[] sorted, int[] freq) {
            this.sorted = sorted;
            this.freq = freq;
        }

        @Override
        public String toString() {

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 256; i++) {
                if (freq[i] > 0) {
                    sb.append("" + (char) i + ":" + freq[i] + ", ");
                }
            }


            return "SortResult{" +
                    "sorted=" + Arrays.toString(sorted) +
                    ", freq=" + sb +
                    '}';
        }
    }

}