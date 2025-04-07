package org.example.model.filters.filters;

import org.example.model.events.FiltrationCompletedEvent;
import org.example.model.filters.Filter;
import org.example.model.filters.FilterPrototype;
import org.example.model.filters.filterModels.ModelPrototype;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

@Filter(descr = "Дизеринг", icon = "")
public class OrderedDither2 extends FilterPrototype {
    private static final int MAX_COLOR_VALUE = 255;
    private static final int COLOR_CHANNELS = 3; // RGB

    private final List<Integer[]> thresholdMatrices = new ArrayList<>();
    private final List<Integer> matrixSizes = new ArrayList<>();

    public OrderedDither2(ModelPrototype filterModel) {
        super(filterModel);
        initializeThresholdMatrices();
    }

    private void initializeThresholdMatrices() {
        // Базовый случай: матрица 1x1
        thresholdMatrices.add(new Integer[]{0});
        matrixSizes.add(1);

        int currentSize = 2;
        for (int i = 0; i < 4; ++i) {
            Integer[] newMatrix = new Integer[currentSize * currentSize];
            Integer[] previousMatrix = thresholdMatrices.get(thresholdMatrices.size() - 1);
            int halfSize = currentSize / 2;

            generateMatrixQuadrants(newMatrix, previousMatrix, currentSize, halfSize);

            thresholdMatrices.add(newMatrix);
            matrixSizes.add(currentSize);
            currentSize *= 2;
        }
    }

    private void generateMatrixQuadrants(Integer[] newMatrix, Integer[] previousMatrix,
                                         int size, int halfSize) {
        // Верхний левый квадрант
        fillQuadrant(newMatrix, previousMatrix, size, halfSize, 0, 0, 0);
        // Верхний правый квадрант
        fillQuadrant(newMatrix, previousMatrix, size, halfSize, 0, halfSize, 2);
        // Нижний левый квадрант
        fillQuadrant(newMatrix, previousMatrix, size, halfSize, halfSize, 0, 3);
        // Нижний правый квадрант
        fillQuadrant(newMatrix, previousMatrix, size, halfSize, halfSize, halfSize, 1);
    }

    private void fillQuadrant(Integer[] newMatrix, Integer[] previousMatrix, int size,
                              int halfSize, int startY, int startX, int offset) {
        for (int y = 0; y < halfSize; ++y) {
            for (int x = 0; x < halfSize; ++x) {
                int newY = startY + y;
                int newX = startX + x;
                int prevIndex = y * halfSize + x;
                newMatrix[newY * size + newX] = 4 * previousMatrix[prevIndex] + offset;
            }
        }
    }

    private int findOptimalMatrixSize(int quantizationLevels) {
        if (quantizationLevels < 2) return 1;
        if (quantizationLevels < 30) return 4;
        if (quantizationLevels < 60) return 3;
        if (quantizationLevels < 90) return 2;
        return 1;
    }

    private ArrayList<Integer> generateColorPalette(int quantizationLevels) {
        ArrayList<Integer> palette = new ArrayList<>();
        if (quantizationLevels <= 1) {
            palette.add(MAX_COLOR_VALUE);
            return palette;
        }

        int step = Math.max(1, MAX_COLOR_VALUE / (quantizationLevels - 1));
        for (int i = 0; i <= MAX_COLOR_VALUE; i += step) {
            palette.add(Math.min(i, MAX_COLOR_VALUE));
        }

        return palette;
    }

    private int applyDithering(int colorValue, int x, int y,
                               ArrayList<Integer> palette,
                               Integer[] matrix, int matrixSize) {
        double threshold = ((double) matrix[y * matrixSize + x]) / (matrixSize * matrixSize);
        double modifiedValue = colorValue + threshold * 256 - 128;

        return findClosestColor(modifiedValue, palette);
    }

    private int findClosestColor(double value, ArrayList<Integer> palette) {
        int closestColor = 0;
        double minDistance = Double.MAX_VALUE;

        for (int color : palette) {
            double distance = Math.abs(color - value);
            if (distance < minDistance) {
                minDistance = distance;
                closestColor = color;
            }
        }

        return closestColor;
    }

    private int processColorChannel(int colorValue, int x, int y,
                                    ArrayList<Integer> palette,
                                    Integer[] matrix, int matrixSize) {
        x %= matrixSize;
        y %= matrixSize;
        return applyDithering(colorValue, x, y, palette, matrix, matrixSize);
    }

    @Override
    public void convert(BufferedImage sourceImage, BufferedImage resultImage) {
        if (sourceImage == null || resultImage == null) {
            throw new IllegalArgumentException("Source and result images cannot be null");
        }

        int[] quantizationLevels = {
                filterModel.getInteger("red quantization number"),
                filterModel.getInteger("green quantization number"),
                filterModel.getInteger("blue quantization number")
        };

        List<ArrayList<Integer>> palettes = new ArrayList<>();
        List<Integer[]> matrix = new ArrayList<>();
        List<Integer> matrixSizes = new ArrayList<>();

        for (int i = 0; i < COLOR_CHANNELS; i++) {
            palettes.add(generateColorPalette(quantizationLevels[i]));
            int matrixIndex = findOptimalMatrixSize(quantizationLevels[i]);
            matrix.add(thresholdMatrices.get(matrixIndex));
            matrixSizes.add(this.matrixSizes.get(matrixIndex));
        }

        processImagePixels(sourceImage, resultImage, palettes, matrix, matrixSizes);
        update(new FiltrationCompletedEvent(resultImage));
    }

    private void processImagePixels(BufferedImage source, BufferedImage result,
                                    List<ArrayList<Integer>> palettes,
                                    List<Integer[]> matrices,
                                    List<Integer> matrixSizes) {
        for (int y = 0; y < source.getHeight(); y++) {
            for (int x = 0; x < source.getWidth(); x++) {
                int rgb = source.getRGB(x, y);

                int red = processColorChannel((rgb >> 16) & 0xFF, x, y,
                        palettes.get(0), matrices.get(0), matrixSizes.get(0));

                int green = processColorChannel((rgb >> 8) & 0xFF, x, y,
                        palettes.get(1), matrices.get(1), matrixSizes.get(1));

                int blue = processColorChannel(rgb & 0xFF, x, y,
                        palettes.get(2), matrices.get(2), matrixSizes.get(2));

                int alpha = (rgb >> 24) & 0xFF;
                int newRgb = (alpha << 24) | (red << 16) | (green << 8) | blue;
                result.setRGB(x, y, newRgb);
            }
        }
    }
}