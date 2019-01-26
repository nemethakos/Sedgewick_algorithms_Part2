public class Util {

    public static void main(String... args) {
        for (int rowNum = 1; rowNum < 4; rowNum++) {

            for (int colNum = 1; colNum < 4; colNum++) {


                for (int row = 0; row < rowNum; row++) {

                    for (int col = 0; col < colNum; col++) {

                        int index = getIndex(row, col, colNum);
                        int rowResult = getRow(index, colNum);
                        int colResult = getCol(index, colNum);

                        if (row != rowResult) {
                            System.out.println("rowNum:" + rowNum + ", colNum:" + colNum);
                            System.out.println("   index:" + index + ", row:" + row + ", rowResult:"
                                                       + rowResult);
                        }
                        if (col != colResult) {
                            System.out.println("rowNum:" + rowNum + ", colNum:" + colNum);
                            System.out.println("   index:" + index + ", col:" + col + ", colResult:"
                                                       + colResult);
                        }
                    }
                }
            }
        }
    }

    public static int getIndex(int row, int col, int cols) {
        return row * cols + col;
    }

    public static int getRow(int index, int cols) {
        return index / cols;
    }

    public static int getCol(int index, int cols) {
        return index % cols;
    }

}
