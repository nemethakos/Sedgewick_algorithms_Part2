import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.StdOut;

import java.awt.Color;
import java.util.Arrays;

/**
 * Optimization possibilities:
 * <ul>
 * <li>Do not store the edges leading from the child pixel to its parent pixel in the seam. It is
 * possible to calculate the path from the calculated distances assigned to each pixel</li>
 * <li>Remove code duplication horizontal/vertical methods by using a flag to indicate if it is
 * vertical or horizontal mode used. In horizontal mode, each x and y coordinates, width and height
 * values are swapped. In this way only one set of methods (e.g.: the vertical ones) needs to be
 * implemented</li>
 * <li>Optimize energy calculation by using two pass (horizontal, vertical) with sliding
 * window</li>
 * </ul>
 * <p>
 * Notes from the specification:
 * <ul>
 * <li>Corner cases. Your code should throw a java.lang.IllegalArgumentException when a constructor
 * or method is called with an invalid argument, as documented below:
 * <ul>
 * <li>By convention, the indices x and y are integers between 0 and width − 1 and between 0 and
 * height − 1 respectively, where width is the width of the current image and height is the height.
 * Throw a java.lang.IllegalArgumentException if either x or y is outside its prescribed
 * range.</li>
 * </ul>
 *
 * @see <a href="https://sp18.datastructur.es/materials/hw/hw5/hw5">https://sp18.datastructur.es/materials/hw/hw5/hw5</a>
 */
public class SeamCarver {

    private Picture picture;

    /**
     * The data type may not mutate the Picture argument to the constructor.
     * <p>
     * Create a seam carver object based on the given picture
     * <p>
     * Throw a java.lang.IllegalArgumentException if the constructor is called with a null
     * argument.
     *
     * @param picture the {@link Picture}
     */
    public SeamCarver(Picture picture) {
        if (picture == null) {
            throw new IllegalArgumentException();
        }
        // make a copy to avoid mutability
        this.picture = new Picture(picture);
    }

    /**
     * current picture
     */
    public Picture picture() {
        // return a copy to avoid mutability
        return new Picture(picture);
    }

    /**
     * energy of pixel at column x and row y
     */
    public double energy(int x, int y) {
        if (x < 0 || y < 0 || x >= picture.width() || y >= picture.height()) {
            throw new IllegalArgumentException(
                    String.format("Wrong x: %d or y: %d for width: %d, height: %d", x, y, width(),
                                  height()));
        }
        if (x == 0 || y == 0 || x == picture.width() - 1 || y == picture.height() - 1) {
            return 1000;
        }

        Color left = picture.get(x - 1, y);
        Color right = picture.get(x + 1, y);
        Color top = picture.get(x, y - 1);
        Color bottom = picture.get(x, y + 1);

        return Math.sqrt(substract(right, left).squared() + substract(bottom, top).squared());
    }

    // width of current picture
    public int width() {
        return picture.width();
    }

    // height of current picture
    public int height() {
        return picture.height();
    }

    /**
     * Returns a {@link ColorResult} containing the substraction of each color component (R, G, B).
     * {@link Color} does not support negative values, so the {@link ColorResult} value object is
     * used instead.
     *
     * @param first  the first {@link Color}
     * @param second the second {@link Color}
     * @return the result of the substraction of the second color from the first color
     */
    private ColorResult substract(Color first, Color second) {
        return new ColorResult(
                first.getRed() - second.getRed(),
                first.getGreen() - second.getGreen(),
                first.getBlue() - second.getBlue()
        );
    }


    /**
     * sequence of indices for horizontal seam
     * <p>
     * The behavior of findHorizontalSeam() is analogous to that of findVerticalSeam() except that
     * it returns an array of length width such that entry x is the row number of the pixel to be
     * removed from column x of the image.
     *
     * @return the horizontal seam
     */
    public int[] findHorizontalSeam() {
        MinimumEnergyPath minimumEnergyPath = new MinimumEnergyPath(this, width(), height(), false);
        Path path = minimumEnergyPath.shortestPathHorizontal();
        return path.steps;
    }


    /**
     * sequence of indices for vertical seam
     * <p>
     * The findVerticalSeam() method returns an array of length H such that entry y is the column
     * number of the pixel to be removed from row y of the image.
     *
     * @return the vertical seam
     */
    public int[] findVerticalSeam() {
        MinimumEnergyPath minimumEnergyPath = new MinimumEnergyPath(this, width(), height(), true);
        Path path = minimumEnergyPath.shortestPathVertical();
        return path.steps;
    }


    /**
     * Remove horizontal seam from current picture
     * <p>
     * Throw a java.lang.IllegalArgumentException if removeVerticalSeam() is called when the width
     * of the picture is less than or equal to 1 or if removeHorizontalSeam() is called when the
     * height of the picture is less than or equal to 1.
     * <p>
     * Throw a java.lang.IllegalArgumentException if removeVerticalSeam() or removeHorizontalSeam()
     * is called with an array of the wrong length or if the array is not a valid seam (i.e., either
     * an entry is outside its prescribed range or two adjacent entries differ by more than 1).
     *
     * @param seam the seam to remove
     */
    public void removeHorizontalSeam(int[] seam) {
        if (height() <= 1) {
            throw new IllegalArgumentException();
        }
        checkSeam(seam, false);

        // create new Picture
        Picture p = new Picture(width(), height() - 1);
        for (int x = 0; x < width(); x++) {
            // copy pixels before the seam
            for (int y = 0; y < seam[x]; y++) {
                p.set(x, y, picture.get(x, y));
            }
            // copy pixels after the seam
            for (int y = seam[x]; y < height() - 1; y++) {
                p.set(x, y, picture.get(x, y + 1));
            }
        }
        picture = p;


    }

    /**
     * Checks the seam if it is valid (non null, length is valid, element values are valid: inside
     * the valid interval, each consecutive value differs maximum by one from the previous one)
     *
     * @param seam     the seam to check
     * @param vertical true if the seam is vertical, false if horizontal
     */
    private void checkSeam(int[] seam, boolean vertical) {
        if (seam == null) {
            throw new IllegalArgumentException();
        }

        int length;
        int max;
        if (!vertical) {
            length = width();
            max = height();
        }
        else {
            length = height();
            max = width();
        }
        if (seam.length != length) {
            throw new IllegalArgumentException(String.format(
                    "seam[] length=%d, required:%d, vertical: %b, width:%d, height: %d",
                    seam.length, length, vertical, width(), height()));
        }

        for (int i = 0; i < seam.length; i++) {
            if (seam[i] < 0 || seam[i] > max) {
                throw new IllegalArgumentException(
                        String.format("Seam array element seam[%d]=%d is out of range[%d,%d]", i,
                                      seam[i], 0, max));
            }
            if (i < seam.length - 1) {
                if (Math.abs(seam[i + 1] - seam[i]) > 1) {
                    throw new IllegalArgumentException(
                            String.format(
                                    "Seam array element seam[%d]=%d and seam[%d]=%d is differ more than 1",
                                    i,
                                    seam[i], i + 1, seam[i + 1]));
                }
            }
        }
    }

    /**
     * remove vertical seam from current picture
     * <p>
     * Throw a java.lang.IllegalArgumentException if removeVerticalSeam() is called when the width
     * of the picture is less than or equal to 1 or if removeHorizontalSeam() is called when the
     * height of the picture is less than or equal to 1.
     * <p>
     * Throw a java.lang.IllegalArgumentException if removeVerticalSeam() or removeHorizontalSeam()
     * * is called with an array of the wrong length or if the array is not a valid seam (i.e.,
     * either an entry is outside its prescribed range or two adjacent entries differ by more than
     * 1).
     *
     * @param seam the seam to remove
     */
    public void removeVerticalSeam(int[] seam) {
        if (width() <= 1) {
            throw new IllegalArgumentException();
        }
        checkSeam(seam, true);
        // create new Picture
        Picture p = new Picture(width() - 1, height());
        for (int y = 0; y < height(); y++) {
            // copy pixels before the seam
            for (int x = 0; x < seam[y]; x++) {
                p.set(x, y, picture.get(x, y));
            }
            // copy pixels after the seam
            for (int x = seam[y]; x < width() - 1; x++) {
                p.set(x, y, picture.get(x + 1, y));
            }
        }
        picture = p;

    }

    /**
     * Value object to store the components of a color
     */
    private static class ColorResult {
        private int r;
        private int g;
        private int b;

        public ColorResult(int r, int g, int b) {
            this.r = r;
            this.g = g;
            this.b = b;
        }

        public double squared() {
            return r * r + g * g + b * b;
        }

    }

    /**
     * Value object for the minimum energy path
     */
    private static class Path {
        private int[] steps;
        private double distance;

        public Path(int[] steps, double distance) {
            this.steps = steps;
            this.distance = distance;
        }

        @Override
        public String toString() {
            return "Path{" +
                    "steps=" + Arrays.toString(steps) +
                    ", distance=" + distance +
                    '}';
        }
    }

    /**
     * Class for holding the data and functionality to find the minimum energy path by relaxation
     */
    private static class MinimumEnergyPath {
        private SeamCarver seamCarver;
        private boolean vertical;
        private double[] distance;
        private double[] energy;
        private int[] edgeTo;
        private int height;
        private int width;

        public MinimumEnergyPath(SeamCarver seamCarver, int width, int height, boolean vertical) {

            this.vertical = vertical;
            this.width = width;
            this.height = height;
            distance = new double[width * height];
            edgeTo = new int[width * height];
            energy = new double[width * height];

            // set initial distances
            Arrays.fill(distance, Double.POSITIVE_INFINITY);

            // set distances/edges to zero/-1 for starting row/column
            if (vertical) {
                for (int x = 0; x < width; x++) {
                    distance[getIndex(x, 0)] = 0;
                    edgeTo[getIndex(x, 0)] = -1;
                }
            }
            else { // horizontal
                for (int y = 0; y < height; y++) {
                    distance[getIndex(0, y)] = 0;
                    edgeTo[getIndex(0, y)] = -1;
                }
            }

            // precalculates the energy array
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    energy[getIndex(x, y)] = seamCarver.energy(x, y);
                }
            }
        }

        /**
         * Returns the index from pixel coordinates
         *
         * @param x the x coordinate
         * @param y the y coordinate
         * @return the index = width * y + x
         */
        private int getIndex(int x, int y) {
            return width * y + x;
        }

        /**
         * Returns the shortest path (minimum energy) in horizontal direction
         *
         * @return the shortest path (minimum energy) in horizontal direction
         */
        private Path shortestPathHorizontal() {
            // leftmost column has distance 0
            for (int x = 1; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    relaxHorizontal(x, y);
                }
            }

            return getPath();
        }

        /**
         * Calculates the distance(energy) to the pixel (childX, childY) in horizontal direction
         *
         * @param childX the pixel's x coordinate
         * @param childY the pixel's y coordinate
         */
        private void relaxHorizontal(int childX, int childY) {
            int parentX = childX - 1;

            for (int parentY = childY - 1; parentY <= childY + 1; parentY++) {

                if (parentY >= 0 && parentY < height) {

                    int childIndex = getIndex(childX, childY);
                    int parentIndex = getIndex(parentX, parentY);

                    double distanceToParentPlusChildEnergy = distance[parentIndex]
                            + energy[childIndex];
                    double childDistance = distance[childIndex];

                    if (childDistance > distanceToParentPlusChildEnergy) {
                        distance[childIndex] = distanceToParentPlusChildEnergy;
                        edgeTo[childIndex] = parentIndex;
                    }
                }
            }
        }

        /**
         * Returns the path from calculated distances
         *
         * @return the {@link Path}
         */
        private Path getPath() {

            double minEnergy = 0;
            int parentIndex;
            int[] result;

            if (vertical) {

                // find minimum end of seam
                int minX = -1;
                double minValue = Double.MAX_VALUE;
                // find minimum distance at the bottom row
                for (int x = 0; x < width; x++) {
                    double currentDistance = distance[getIndex(x, height - 1)];
                    if (currentDistance < minValue) {
                        minValue = currentDistance;
                        minX = x;
                    }
                }

                result = new int[height];
                parentIndex = getIndex(minX, height - 1);
                int y = height - 1;
                while (parentIndex >= 0) {
                    result[y--] = getX(parentIndex);
                    parentIndex = edgeTo[parentIndex];
                }

            }

            else {
                // horizontal path

                int minY = -1;
                double minValue = Double.MAX_VALUE;
                // find minimum distance at the leftmost row
                for (int y = 0; y < height; y++) {
                    double currentDistance = distance[getIndex(width - 1, y)];
                    if (currentDistance < minValue) {
                        minValue = currentDistance;
                        minY = y;
                    }
                }

                result = new int[width];
                parentIndex = getIndex(width - 1, minY);
                int x = width - 1;
                while (parentIndex >= 0) {
                    result[x--] = getY(parentIndex);
                    parentIndex = edgeTo[parentIndex];
                }
            }
            return new Path(result, minEnergy);
        }

        /**
         * Returns the x coordinate from the index
         *
         * @param index the index
         * @return the x coordinate from the index
         */
        private int getX(int index) {
            return index % width;
        }

        /**
         * Returns the y coordinate from the index
         *
         * @param index the index
         * @return the y coordinate from the index
         */
        private int getY(int index) {
            return index / width;
        }

        /**
         * Calculates the minimum distances to each pixel by relaxing each pixels (edges leading
         * from the pixel to its parent pixels)
         *
         * @return the minimum energy {@link Path}
         */
        public Path shortestPathVertical() {
            // the first row has 0 distances
            for (int y = 1; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    relaxVertical(x, y);
                }
            }

            return getPath();
        }

        /**
         * Calculates the minimum distance (and edge to the parent) to the pixel (childX, childY)
         *
         * @param childX the x coordinate of the pixel
         * @param childY the y coordinate of the pixel
         */
        private void relaxVertical(int childX, int childY) {
            int parentY = childY - 1;

            for (int parentX = childX - 1; parentX <= childX + 1; parentX++) {

                if (parentX >= 0 && parentX < width) {

                    int childIndex = getIndex(childX, childY);
                    int parentIndex = getIndex(parentX, parentY);

                    double parentDistancePlusEnergy = distance[parentIndex] + energy[childIndex];
                    double childDistance = distance[childIndex];

                    if (childDistance > parentDistancePlusEnergy) {
                        distance[childIndex] = parentDistancePlusEnergy;
                        edgeTo[childIndex] = parentIndex;
                    }
                }
            }
        }


        /**
         * Prints the distance for each pixel, with an asterisk when it is on the minimum energy
         * path and the direction to the parent pixel (left-up:"\", up:"|", right-up: "/"
         */
        public void print() {
            System.out.println();
            Path path = getPath();
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    boolean isOnPath = false;

                    if (vertical) {
                        if (path.steps[y] == x) {
                            isOnPath = true;
                        }
                    }
                    else {
                        if (path.steps[x] == y) {
                            isOnPath = true;
                        }
                    }

                    int index = edgeTo[getIndex(x, y)];
                    String arrow;
                    if (vertical) {
                        if (getX(index) < x) {
                            arrow = "\\";
                        }
                        else if (getX(index) == x) {
                            arrow = "|";
                        }
                        else {
                            arrow = "/";
                        }
                    }
                    else {
                        if (getY(index) < y) {
                            arrow = "/";
                        }
                        else if (getY(index) == y) {
                            arrow = "-";
                        }
                        else {
                            arrow = "\\";
                        }
                    }

                    String pathStr = " ";
                    if (isOnPath) {
                        pathStr = "*";
                    }

                    StdOut.printf("%8.2f%s%s ", distance[getIndex(x, y)], arrow, pathStr);
                    //  StdOut.printf("%3d ", edgeTo[getIndex(x, y)]);
                }
                StdOut.println();
            }
        }
    }
}
