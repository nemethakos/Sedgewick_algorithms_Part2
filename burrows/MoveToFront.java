import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {
    // if args[0] is '-', apply move-to-front encoding
    // if args[0] is '+', apply move-to-front decoding
    public static void main(String[] args) {

        if (args.length == 0) {
            throw new IllegalArgumentException("No flag (- or +) specified!");
        }
        String flag = args[0];
        switch (flag) {
            case "-":
                encode();
                break;
            case "+":
                decode();
                break;
            default:
                System.out.println("Only + or - is allowed!");

        }
    }

    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {

        int[] alphabet = getAlphabet();

        while (!BinaryStdIn.isEmpty()) {
            int character = BinaryStdIn.readByte();
            int out = moveToFront(alphabet, character);
            BinaryStdOut.write((char) out);
        }
        BinaryStdOut.close();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output

    /**
     * <p>Move-to-front decoding. Initialize an ordered sequence of 256 characters, where extended
     * ASCII character i appears ith in the sequence.
     * <p>
     * Now, <ul>
     * <li>read each 8-bit character i (but treat it as an integer between 0 and 255) from standard
     * input one at a time,</li>
     * <li>write the ith character in the sequence, and</li>
     * <li>move that character to the front.
     * </ul>
     */
    public static void decode() {

        int[] alphabet = getAlphabet();

        while (!BinaryStdIn.isEmpty()) {
            int index = BinaryStdIn.readByte();
            char character = (char) alphabet[index];
            BinaryStdOut.write(character);
            int out = moveToFront(alphabet, character);
        }
        BinaryStdOut.close();
    }

    private static int[] getAlphabet() {
        int[] alphabet = new int[256];
        for (int i = 0; i < 256; i++) {
            alphabet[i] = i;
        }
        return alphabet;
    }

    private static void moveToFrontOneStep(int[] alphabet, int in, int from, int to) {
        int out = moveToFront(alphabet, in);
        String buf = printBuffer(alphabet, from, to);
        System.out.println(String.format("in: %c, out: %2d, buf: %s", in, out, buf));
    }

    private static int moveToFront(int[] alphabet, int character) {
        // get old index
        int index = getIndex(alphabet, character);

        if (index > 0) {

            int temp = alphabet[index];

            System.arraycopy(alphabet, 0, alphabet, 1, index);
            alphabet[0] = temp;

        }

        return index;

    }

    private static String printBuffer(int[] alphabet, int from, int to) {
        StringBuilder sb = new StringBuilder();
        for (int i = from; i <= to; i++) {
            int character = alphabet[i];
            if (character <= ' ') {
                character = '_';
            }
            sb.append(String.format("%c", character));
        }
        return sb.toString();
    }

    private static int getIndex(int[] alphabet, int character) {
        int index = -1;
        for (int i = 0; i < 256; i++) {
            if (alphabet[i] == character) {
                index = i;
                break;
            }
        }
        return index;
    }
}
