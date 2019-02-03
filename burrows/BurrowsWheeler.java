import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

import java.util.Arrays;

public class BurrowsWheeler {

    private static final int MAX_BYTE_VALUE = 255;
    private static final int RADIX = 256;

    /**
     * <p>Apply Burrows-Wheeler transform, reading from standard input and writing to standard
     * output
     * <p>if args[0] is '-', apply Burrows-Wheeler transform
     * <p>if args[0] is '+', apply Burrows-Wheeler inverse transform
     *
     * @param args program arguments: first argument is + for decoding, - for encoding
     */
    public static void main(String[] args) {

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
            BinaryStdOut.write((byte) (getLastCharOfStringAtIndex(s, index[i]) & MAX_BYTE_VALUE));
        }

        BinaryStdOut.close();
    }

    /**
     * Apply Burrows-Wheeler inverse transform, reading from standard input and writing to standard
     * output
     */
    public static void inverseTransform() {

        int first = BinaryStdIn.readInt();

        char[] t = BinaryStdIn.readString().toCharArray();

        // the firsts array contains the characters of t in sorted order
        char[] plain = decode(t, first);

        for (int i = 0; i < plain.length; i++) {
            BinaryStdOut.write((byte) (plain[i] & MAX_BYTE_VALUE));
        }

        BinaryStdOut.close();
    }

    private static char getLastCharOfStringAtIndex(String s, int index) {
        return s.charAt((index + s.length() - 1) % s.length());
    }

    /**
     * Decodes the encoded char array using the value first
     *
     * @param t     the encoded array
     * @param first the first value provided by the encoder
     * @return the decoded array
     */
    private static char[] decode(char[] t, int first) {

        SortResult sr = sort(t);

        int[] next = getNext(t, sr.sorted, sr.freq);

        char[] output = new char[t.length];
        int current = first;
        for (int i = 0; i < output.length; i++) {
            output[i] = sr.sorted[current];
            current = next[current];
        }

        return output;
    }

    /**
     * LSD Radix sort & frequency counting of the 8-bit alphabet
     *
     * @param a character array
     * @return sorted character array
     */
    private static SortResult sort(char[] a) {
        int n = a.length;
        int radix = RADIX;   // extend ASCII alphabet size
        char[] aux = new char[n];
        int[] freq = new int[radix];

        // compute frequency counts
        int[] count = new int[radix + 1];
        for (int i = 0; i < n; i++) {
            count[a[i] + 1]++;
            freq[a[i]]++;
        }

        // compute cumulates
        for (int r = 0; r < radix; r++)
            count[r + 1] += count[r];

        // move data
        for (int i = 0; i < n; i++)
            aux[count[a[i]]++] = a[i];

        return new SortResult(aux, freq);

    }

    /**
     * Returns the next character for the BW decoder algorithm
     *
     * @param t      the last character of the suffix array
     * @param firsts the first character of the suffix array
     * @param freq   the frequency count of the alphabet
     * @return the "next" array for the BW decoder
     */
    private static int[] getNext(char[] t, char[] firsts, int[] freq) {
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

        return next;
    }

    /**
     * Result of sorting and frequency counting
     */
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