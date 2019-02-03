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
                break;
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

    /**
     * <p></p>Apply move-to-front decoding, reading from standard input and writing to standard
     * output
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
            moveToFront(alphabet, (byte) (value & MAX_BYTE_VALUE));
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

    /**
     * Moves the <code>character</code> to the front of array <code>alphabet</code>
     *
     * @param alphabet  the alphabet array
     * @param character the character to move to the front
     * @return the index of character before moving to front
     */
    private static int moveToFront(byte[] alphabet, byte character) {
        // get old index
        int index = getIndex(alphabet, character);

        byte temp = alphabet[index];

        System.arraycopy(alphabet, 0, alphabet, 1, index);
        alphabet[0] = temp;

        return index;
    }

    /**
     * Linear search for the character in the alphabet array
     *
     * @param alphabet  the alphabet array
     * @param character the character to search for
     * @return the index of character in array
     */
    private static int getIndex(byte[] alphabet, int character) {
        for (int i = 0; i < ALPHABET_SIZE; i++) {
            if ((alphabet[i] & MAX_BYTE_VALUE) == (character & MAX_BYTE_VALUE)) {
                return i;
            }
        }
        throw new IllegalArgumentException();
    }
}
