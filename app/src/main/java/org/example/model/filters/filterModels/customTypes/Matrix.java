package org.example.model.filters.filterModels.customTypes;

import java.util.Arrays;

// maybe we sho
public class Matrix {
    private int width;
    private int height;

    private int[] data;

    public Matrix(int width, int height) {
        this.width = width;
        this.height = height;
        this.data = new int[width * height];
    }

    public void set(int x, int y, int item) {
        if (x * width + y >= width * height) {
            return;
        }

        data[x * width + y] = item;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int get(int x, int y) {
        if (x * width + y < width * height) {
            return data[x * width + y];
        }

        throw new RuntimeException("Invalid index");
    }

    public int safetyGet(int x, int y) {
        if (x * width + y < width * height) {
            return data[x * width + y];
        }

        return 0;
    }


    public void resize(int newWidth, int newHeight) {
        data = Arrays.copyOf(data, newWidth * newHeight);
        width = newWidth;
        height = newHeight;
    }
}
