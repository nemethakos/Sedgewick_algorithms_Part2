/**
 * Represents one step in finding a valid word
 */
public class Step {

    /**
     * Number of columns in the table, only for {@link #toString()}
     */
    private final int cols;

    /**
     * index of cell in the table
     */
    private final int index;

    /**
     * The character
     */
    private final char character;

    public Step(int index, char character, int cols) {
        this.character = character;
        this.index = index;
        this.cols = cols;
    }

    public int getIndex() {
        return index;
    }

    public char getCharacter() {
        return character;
    }

    @Override
    public String toString() {
        return character + "(" + Util.getRow(index, cols) + "," + Util.getCol(index, cols) + ")";
    }
}
