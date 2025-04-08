package org.example.model.filters.filters;

import org.example.model.events.FiltrationCompletedEvent;
import org.example.model.filters.Filter;
import org.example.model.filters.FilterPrototype;
import org.example.model.filters.filterModels.ModelPrototype;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

@Filter(descr = "Адаптивный упорядоченный дизеринг с гамма-коррекцией", icon = "/utils/ordered_dithering.png")
public class OrderedDither2 extends FilterPrototype {
    // Предопределенные матрицы Байера разных размеров
    private static final int[][] BAYER_MATRIX_2x2 = {
            {0, 2},
            {3, 1}
    };

    private static final int[][] BAYER_MATRIX_4x4 = {
            { 0,  8,  2, 10},
            {12,  4, 14,  6},
            { 3, 11,  1,  9},
            {15,  7, 13,  5}
    };

    private static final int[][] BAYER_MATRIX_8x8 = {
            { 0, 32,  8, 40,  2, 34, 10, 42},
            {48, 16, 56, 24, 50, 18, 58, 26},
            {12, 44,  4, 36, 14, 46,  6, 38},
            {60, 28, 52, 20, 62, 30, 54, 22},
            { 3, 35, 11, 43,  1, 33,  9, 41},
            {51, 19, 59, 27, 49, 17, 57, 25},
            {15, 47,  7, 39, 13, 45,  5, 37},
            {63, 31, 55, 23, 61, 29, 53, 21}
    };

    // Кэш матриц для быстрого доступа
    private static final Map<Integer, int[][]> BAYER_MATRICES = new HashMap<>();

    static {
        BAYER_MATRICES.put(2, BAYER_MATRIX_2x2);
        BAYER_MATRICES.put(4, BAYER_MATRIX_4x4);
        BAYER_MATRICES.put(8, BAYER_MATRIX_8x8);
    }

    public OrderedDither2(ModelPrototype filterModel) {
        super(filterModel);
    }

    @Override
    public void convert(BufferedImage image, BufferedImage outputImage) {
        if (image == null) {
            throw new IllegalArgumentException("Image cannot be null");
        }

        int width = image.getWidth();
        int height = image.getHeight();

        int matrixSize = determineOptimalMatrixSize(width, height);
        int[][] bayerMatrix = BAYER_MATRICES.get(matrixSize);
        double matrixNormalizer = 1.0 / (matrixSize * matrixSize);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                outputImage.setRGB(x, y, image.getRGB(x, y));
            }
        }

        // Применяем дизеринг
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = outputImage.getRGB(x, y);
                int alpha = (argb >> 24) & 255;
                int red = (argb >> 16) & 255;
                int green = (argb >> 8) & 255;
                int blue = argb & 255;

                int newRed = applyDithering(red, x, y, "red level", bayerMatrix, matrixSize, matrixNormalizer);
                int newGreen = applyDithering(green, x, y, "green level", bayerMatrix, matrixSize, matrixNormalizer);
                int newBlue = applyDithering(blue, x, y, "blue level", bayerMatrix, matrixSize, matrixNormalizer);

                int newARGB = (alpha << 24) | (newRed << 16) | (newGreen << 8) | newBlue;
                outputImage.setRGB(x, y, newARGB);
            }
        }

        update(new FiltrationCompletedEvent(outputImage));
    }

    private int determineOptimalMatrixSize(int width, int height) {
        int maxDimension = Math.max(width, height);
        if (maxDimension <= 256) return 2;
        if (maxDimension <= 1024) return 4;
        return 8;
    }

    private int applyDithering(int oldColor, int x, int y, String levelKey,
                               int[][] bayerMatrix, int matrixSize, double matrixNormalizer) {
        int levels = filterModel.getInteger(levelKey) + 1;
        if (levels <= 1) return oldColor;

        double linearColor = srgbToLinear(oldColor / 255.0);
        int threshold = bayerMatrix[y % matrixSize][x % matrixSize];
        double ditherThreshold = threshold * matrixNormalizer;

        int quantized = (int)(linearColor * (levels - 1) + ditherThreshold);
        quantized = Math.min(quantized, levels - 1);

        double srgb = linearToSrgb(quantized / (double)(levels - 1));
        return clamp((int)(srgb * 255), 0, 255);
    }

    private double srgbToLinear(double srgb) {
        return srgb <= 0.04045 ? srgb / 12.92 : Math.pow((srgb + 0.055) / 1.055, 2.4);
    }

    private double linearToSrgb(double linear) {
        return linear <= 0.0031308 ? 12.92 * linear : 1.055 * Math.pow(linear, 1/2.4) - 0.055;
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value + 10));
    }
}