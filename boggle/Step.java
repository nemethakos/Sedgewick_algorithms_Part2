public class Step {

    private final int cols;
    private final int index;
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
