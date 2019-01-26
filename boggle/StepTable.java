import java.util.ArrayList;
import java.util.List;

public class StepTable {
    private int rows;
    private int cols;
    private List<List<Step>> steps = new ArrayList<>();
    private char[] chars;
    private boolean[] visited;

    public StepTable(BoggleBoard bb) {

        this.rows = bb.rows();
        this.cols = bb.cols();

        this.chars = new char[rows * cols];
        this.visited = new boolean[rows * cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                chars[Util.getIndex(r, c, cols)] = bb.getLetter(r, c);

                ArrayList<Step> sl = new ArrayList<>();

                for (int rr = r - 1; rr <= r + 1; rr++) {
                    for (int cc = c - 1; cc <= c + 1; cc++) {
                        if (isValid(r, c, rr, cc, rows, cols)) {
                            sl.add(new Step(Util.getIndex(rr, cc, cols),
                                            bb.getLetter(rr, cc), cols));
                        }
                    }
                }

                steps.add(sl);
            }
        }
    }

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
