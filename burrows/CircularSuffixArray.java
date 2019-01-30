import java.util.Arrays;

public class CircularSuffixArray {

    private int[] index;

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null) {
            throw new IllegalArgumentException();
        }

        index = lsdSort(s);
    }

    private static int[] lsdSort(String suffix) {
        int suffixLength = suffix.length();

        int[] index = new int[suffixLength];

        // initialize index
        for (int i = 0; i < index.length; i++) {
            index[i] = i;
        }

        int[] aux = new int[suffixLength];

        int radix = 256;

        int[] count = new int[radix + 1];

        for (int characterIndexToSort = suffixLength - 1; characterIndexToSort >= 0; characterIndexToSort--) {
            // reuse count
            Arrays.fill(count,0);

            // compute frequency counts
            for (int i = 0; i < suffixLength; i++) {
                int offset = index[i] + characterIndexToSort;
                char c = suffix.charAt(offset % suffixLength);
                //char ithStringsDthChar = getIthStringsDthChar(suffix, index[i] + characterIndexToSort);
                count[c + 1]++;
            }

            // transform count to indices
            for (int r = 0; r < radix; r++) {
                count[r + 1] += count[r];
            }

            // distribute
            for (int i = 0; i < suffixLength; i++) {
                int offset = index[i] + characterIndexToSort;
                int pos = count[suffix.charAt(offset % suffixLength)]++;
                //int pos = count[getIthStringsDthChar(suffix, index[i] + characterIndexToSort)]++;
                aux[pos] = index[i];
            }
            // copy back
            for (int i = 0; i < suffixLength; i++) {
                index[i] = aux[i];
            }
        }

        return index;
    }

    private static char getIthStringsDthChar(String base, int offset) {
        return base.charAt(offset % base.length());
    }

    private static String getStringFromBase(String base, int offset) {
        StringBuilder sb = new StringBuilder();
        for (int i = offset; i < offset + base.length(); i++) {
            sb.append(getIthStringsDthChar(base, i));
        }
        return sb.toString();
    }

    // unit testing (required)
    public static void main(String[] args) {
        String base = "ABRACADABRA!";

        CircularSuffixArray csa = new CircularSuffixArray(base);

        System.out.println("length: "+csa.length());

        for (int i=0; i<csa.length(); i++) {
            System.out.println(csa.index[i]);
        }
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
}