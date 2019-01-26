import java.util.ArrayList;
import java.util.List;

/**
 * Represents the the precomputed steps which are accessible from a cell in the boggle table
 */
public class StepTable {
    /**
     * Number of rows
     */
    private int rows;

    /**
     * Number of columns
     */
    private int cols;

    /**
     * Table of {@link List} of {@link Step}s accessible from the cells of the table, flattened into
     * 1d list
     */
    private List<List<Step>> steps = new ArrayList<>();

    /**
     * Table of characters flattened into 1d array
     */
    private char[] chars;

    /**
     * Table of visited flags flattened into 1d array
     */
    private boolean[] visited;

    public StepTable(BoggleBoard board) {

        this.rows = board.rows();
        this.cols = board.cols();

        this.chars = new char[rows * cols];
        this.visited = new boolean[rows * cols];

        for (int currentRow = 0; currentRow < rows; currentRow++) {
            for (int currentColumn = 0; currentColumn < cols; currentColumn++) {

                int currentIndex = Util.getIndex(currentRow, currentColumn, cols);

                chars[currentIndex] = board.getLetter(currentRow, currentColumn);

                ArrayList<Step> stepList = new ArrayList<>();

                for (int accessibleRow = currentRow - 1; accessibleRow <= currentRow + 1;
                     accessibleRow++) {
                    for (int accessibleColumn = currentColumn - 1;
                         accessibleColumn <= currentColumn + 1; accessibleColumn++) {

                        if (isValid(currentRow, currentColumn, accessibleRow, accessibleColumn,
                                    rows, cols)) {

                            int accessibleIndex = Util
                                    .getIndex(accessibleRow, accessibleColumn, cols);
                            char accessibleLetter = board
                                    .getLetter(accessibleRow, accessibleColumn);
                            Step step = new Step(accessibleIndex, accessibleLetter, cols);

                            stepList.add(step);
                        }
                    }
                }

                steps.add(stepList);
            }
        }
    }

    /**
     * returns true if the cell at (row,col)  is a valid step from (currentRow, currentColumn)
     *
     * @param currentRow startin row
     * @param currentCol starting column
     * @param row        the row of the cell to test the validity of
     * @param col        the column of the cell to test the validity of
     * @param rowNum     the number of rows
     * @param colNum     the number of columns
     * @return true if the cell is validly accessible from (currentRow, currentColumn)
     */
    private static boolean isValid(int currentRow, int currentCol, int row, int col, int rowNum,
                                   int colNum) {
        boolean result = false;
        if (row >= 0 &&
                row < rowNum &&
                col >= 0 &&
                col < colNum &&
                !(row == currentRow && col == currentCol)) {
            result = true;
        }

        return result;
    }

    public static void main(String... args) {

        //  isValid(0,0,1,0,4,4);
        StepTable st = new StepTable(new BoggleBoard(1, 29));
        System.out.println(st);
        System.out.println(st.getAvailableNextSteps(
                new Step(0, st.getChar(0, 0), st.getCols())));

    }

    public List<Step> getAvailableNextSteps(Step step) {
        return steps.get(step.getIndex());
    }

    public char getChar(int row, int col) {
        return chars[Util.getIndex(row, col, cols)];
    }

    public int getCols() {
        return cols;
    }

    public int getRows() {
        return rows;
    }

    public boolean isVisited(Step step) {
        return visited[step.getIndex()];
    }

    public void visit(Step step, boolean set) {
        visited[step.getIndex()] = set;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                sb.append(chars[Util.getIndex(r, c, cols)]);
            }
            sb.append("\n");
        }

        int index = 0;
        for (List<Step> stepList : steps) {
            sb.append("(" + Util.getRow(index, cols) + "," + Util.getCol(index, cols) + "): ");
            for (Step step : stepList) {
                sb.append(step + " ");
            }
            index++;
            sb.append("\n");
        }

        return "StepTable{" + rows + "x" + cols +
                "\n" + sb.toString() + '}';
    }


}
