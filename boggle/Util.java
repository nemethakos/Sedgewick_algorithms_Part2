public class Util {

    /**
     * Returns the index from coordinates
     *
     * @param row  the row number
     * @param col  the column number
     * @param cols number of columns in the table
     * @return the index
     */
    public static int getIndex(int row, int col, int cols) {
        return row * cols + col;
    }

    /**
     * Returns the row number from index
     *
     * @param index the index
     * @param cols  the number of columns
     * @return the row number
     */
    public static int getRow(int index, int cols) {
        return index / cols;
    }

    /**
     * Returns the column number from the index
     *
     * @param index the index
     * @param cols  the column number
     * @return the column number from the index
     */
    public static int getCol(int index, int cols) {
        return index % cols;
    }

}
