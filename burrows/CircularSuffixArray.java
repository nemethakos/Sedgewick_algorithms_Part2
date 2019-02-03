public class CircularSuffixArray {

    private final char[] s;
    private final int length;
    private final int[] index;

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null) {
            throw new IllegalArgumentException();
        }
        this.s = s.toCharArray();
        this.length = s.length();
        this.index = new int[length];

        // init index
        for (int i = 0; i < length; i++) {
            index[i] = i;
        }
        sort(0, length - 1, 0);
    }

    // unit testing (required)
    public static void main(String[] args) {

        String base = "abcd\u00FF\u00FE\u00FD\u0000\u0001\u0002efgh";

        CircularSuffixArray csa = new CircularSuffixArray(base);

        System.out.println("length: " + csa.length());

        for (int i = 0; i < csa.length(); i++) {
            System.out.println(csa.index[i]);
            System.out.println(getStringFromBase(base, csa.index(i)));
        }
    }

    private static String getStringFromBase(String base, int offset) {
        StringBuilder sb = new StringBuilder();
        for (int i = offset; i < offset + base.length(); i++) {
            char character = base.charAt(i % base.length());
            String str = "" + character;
            if (character < 32 || character > 126) {
                str = "0x" + Integer.toString(character, 16);
            }
            sb.append(str + " ");
        }
        return sb.toString();
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

    // exchange index[i] and index[j]
    private void exch(int i, int j) {
        int temp = index[i];
        index[i] = index[j];
        index[j] = temp;
    }

    // return the dth character of ithString, -1 if dthChar = length of ithString
    private int charAt(int ithString, int dthChar) {
        if (dthChar == length) return -1;
        return this.s[((ithString + dthChar) % length)];
    }

    // 3-way string quicksort index[lo..hi] starting at dth character
    private void sort(int lo, int hi, int d) {

        if (hi <= lo || d >= length) {
            return;
        }

        int lt = lo;
        int gt = hi;
        int v = charAt(index[lo], d);
        int i = lo + 1;

        while (i <= gt) {
            int t = charAt(index[i], d);
            if (t < v) {
                exch(lt++, i++);
            }
            else if (t > v) {
                exch(i, gt--);
            }
            else {
                i++;
            }
        }

        // index[lo..lt-1] < v = index[lt..gt] < index[gt+1..hi].
        sort(lo, lt - 1, d);
        if (v >= 0) {
            sort(lt, gt, d + 1);
        }
        sort(gt + 1, hi, d);
    }
}