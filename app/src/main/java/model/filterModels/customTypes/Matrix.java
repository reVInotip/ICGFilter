package model.filterModels.customTypes;

public class Matrix {
    private final int width;
    private final int height;

    private int[] data;

    public Matrix(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void set(int x, int y, int item) {
        if (x * width + y >= width * height) {
            return;
        }

        data[x * width + y] = item;
    }

    public int get(int x, int y) {
        if (x * width + y < width * height) {
            return data[x * width + y];
        }

        return 0;
    }
}
