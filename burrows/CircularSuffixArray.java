import java.util.Arrays;

public class CircularSuffixArray {

    private static final int MAX_BYTE_VALUE = 255;
   // String base;
    private int[] index;

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null) {
            throw new IllegalArgumentException();
        }
        index = lsdSort(s);
    }

    private static int[] lsdSort(String suffixStr) {

        // use char array instead of String
        char[] suffix = suffixStr.toCharArray();

        int suffixLength = suffix.length;

        int[] index = new int[suffixLength];

        // initialize index
        for (int i = 0; i < index.length; i++) {
            index[i] = i;
        }

        int[] aux = new int[suffixLength];

        int radix = 256;

        int[] count = new int[radix + 1];

        for (int characterIndexToSort = suffixLength - 1; characterIndexToSort >= 0;
             characterIndexToSort--) {
            // reuse count
            Arrays.fill(count, 0);

            // compute frequency counts
            for (int i = 0; i < suffixLength; i++) {
                int offset = index[i] + characterIndexToSort;
                int c = (suffix[offset % suffixLength] & MAX_BYTE_VALUE);
                count[c + 1]++;
            }

            // transform count to indices
            for (int r = 0; r < radix; r++) {
                count[r + 1] += count[r];
            }

            // distribute
            for (int i = 0; i < suffixLength; i++) {
                int offset = index[i] + characterIndexToSort;
                int c = (suffix[offset % suffixLength] & MAX_BYTE_VALUE);
                int pos = count[c]++;
                aux[pos] = index[i];
            }
            // copy back
            for (int i = 0; i < suffixLength; i++) {
                index[i] = aux[i];
            }
        }

        return index;
    }

    // length of s
    public int length() {
        return index.length;
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if (i < 0 || i > index.length - 1) {
            throw new IllegalArgumentException();
        }
        return index[i];
    }

    // unit testing (required)
    public static void main(String[] args) {

       String base = "abcd\u00FF\u00FE\u00FD\u0000\u0001\u0002efgh";

        CircularSuffixArray csa = new CircularSuffixArray(base);

       System.out.println("length: " + csa.length());

        for (int i = 0; i < csa.length(); i++) {
            System.out.println(csa.index[i]);
            System.out.println(Util.getStringFromBase(base, csa.index(i)));
        }

    }
}