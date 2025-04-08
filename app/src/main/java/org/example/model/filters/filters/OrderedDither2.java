package org.example.model.filters.filters;

import org.example.model.events.FiltrationCompletedEvent;
import org.example.model.filters.Filter;
import org.example.model.filters.FilterPrototype;
import org.example.model.filters.filterModels.ModelPrototype;

import java.awt.image.BufferedImage;

@Filter(descr = "Упорядоченный дизеринг (матрица Байера 8x8)", icon = "/utils/ordered_dithering.png")
public class OrderedDither2 extends FilterPrototype {
    // Оптимизированная матрица Байера 8x8 (значения 0-63)
    private static final int[][] BAYER_MATRIX_8x8 = {
            {  0, 32,  8, 40,  2, 34, 10, 42 },
            { 48, 16, 56, 24, 50, 18, 58, 26 },
            { 12, 44,  4, 36, 14, 46,  6, 38 },
            { 60, 28, 52, 20, 62, 30, 54, 22 },
            {  3, 35, 11, 43,  1, 33,  9, 41 },
            { 51, 19, 59, 27, 49, 17, 57, 25 },
            { 15, 47,  7, 39, 13, 45,  5, 37 },
            { 63, 31, 55, 23, 61, 29, 53, 21 }
    };

    public OrderedDither2 (ModelPrototype filterModel) {
        super(filterModel);
    }

    @Override
    public void convert(BufferedImage image, BufferedImage outputImage) {
        if (image == null) {
            throw new IllegalArgumentException("Image cannot be null");
        }

        int width = image.getWidth();
        int height = image.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                outputImage.setRGB(x, y, image.getRGB(x, y));
            }
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = outputImage.getRGB(x, y);
                int alpha = (argb >> 24) & 255;
                int red = (argb >> 16) & 255;
                int green = (argb >> 8) & 255;
                int blue = argb & 255;

                int newRed = applyDithering(red, x, y, "red level");
                int newGreen = applyDithering(green, x, y, "green level");
                int newBlue = applyDithering(blue, x, y, "blue level");

                int newARGB = (alpha << 24) | (newRed << 16) | (newGreen << 8) | newBlue;
                outputImage.setRGB(x, y, newARGB);
            }
        }

        update(new FiltrationCompletedEvent(outputImage));
    }

    private int applyDithering(int oldColor, int x, int y, String levelKey) {
        int levels = filterModel.getInteger(levelKey) + 1;
        if (levels <= 1) return oldColor;

        // Гамма-коррекция из статьи
        // вот ссылка если понадобится(https://nigeltao.github.io/blog/2022/gamma-aware-ordered-dithering.html)
        double linearColor = srgbToLinear(oldColor / 255.0);

        int threshold = BAYER_MATRIX_8x8[y % 8][x % 8];
        double ditherThreshold = threshold / 64.0;

        int quantized = (int)(linearColor * (levels - 1) + ditherThreshold);
        quantized = Math.min(quantized, levels - 1);

        // Обратная гамма-коррекция
        double srgb = linearToSrgb(quantized / (double)(levels - 1));
        return clamp((int)(srgb * 255), 0, 255);
    }

    private double srgbToLinear(double srgb) {
        return srgb <= 0.04045
                ? srgb / 12.92
                : Math.pow((srgb + 0.055) / 1.055, 2.4);
    }

    private double linearToSrgb(double linear) {
        return linear <= 0.0031308
                ? 12.92 * linear
                : 1.055 * Math.pow(linear, 1/2.4) - 0.055;
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}