import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {

    private static final short ALPHABET_SIZE = 256;
    private static final short MAX_BYTE_VALUE = 255;

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

        byte[] alphabet = getAlphabet();

        while (!BinaryStdIn.isEmpty()) {
            int value = BinaryStdIn.readChar();
            int out = moveToFront(alphabet, (byte) (value & MAX_BYTE_VALUE));
            BinaryStdOut.write((byte) (out & MAX_BYTE_VALUE));
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

        byte[] alphabet = getAlphabet();

        while (!BinaryStdIn.isEmpty()) {
            int index = Byte.toUnsignedInt(BinaryStdIn.readByte());
            int value = alphabet[index];
            BinaryStdOut.write((byte) (value & MAX_BYTE_VALUE));
            int out = moveToFront(alphabet, (byte) (value & MAX_BYTE_VALUE));
        }
        BinaryStdOut.close();
    }

    private static byte[] getAlphabet() {
        byte[] alphabet = new byte[ALPHABET_SIZE];
        for (int i = 0; i < ALPHABET_SIZE; i++) {
            alphabet[i] = (byte) (i & MAX_BYTE_VALUE);
        }
        return alphabet;
    }

    private static void moveToFrontOneStep(byte[] alphabet, byte in, int from, int to) {
        int out = moveToFront(alphabet, in);
        String buf = printBuffer(alphabet, from, to);
        System.out.println(String.format("in: %c, out: %2d, buf: %s", in, out, buf));
    }

    private static int moveToFront(byte[] alphabet, byte character) {
        // get old index
        int index = getIndex(alphabet, character);

        byte temp = alphabet[index];

        System.arraycopy(alphabet, 0, alphabet, 1, index);
        alphabet[0] = temp;

        return index;

    }

    private static String printBuffer(byte[] alphabet, int from, int to) {
        StringBuilder sb = new StringBuilder();
        for (int i = from; i <= to; i++) {
            char character = (char) (alphabet[i] & MAX_BYTE_VALUE);
            if ((character & MAX_BYTE_VALUE) <= ' ') {
                character = '_';
            }
            sb.append(String.format("%c", character));
        }
        return sb.toString();
    }

    private static int getIndex(byte[] alphabet, int character) {
        for (int i = 0; i < ALPHABET_SIZE; i++) {
            if ((alphabet[i] & MAX_BYTE_VALUE) == (character & MAX_BYTE_VALUE)) {
                return i;
            }
        }
        throw new IllegalArgumentException();
    }
}
