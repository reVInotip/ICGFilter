package org.example.model.filters.filters;

import org.example.model.events.FiltrationCompletedEvent;
import org.example.model.filters.Filter;
import org.example.model.filters.FilterPrototype;
import org.example.model.filters.filterModels.ModelPrototype;

import java.awt.image.BufferedImage;

@Filter(descr = "Дизеринг Флойда-Стейнберга", icon = "/utils/floydSteinbergDithering.png")
public class FloydSteinbergDithering extends FilterPrototype {
    public FloydSteinbergDithering(ModelPrototype filterModel) {
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
                int alpha = argb >> 24 & 255;
                int red = argb >> 16 & 255;
                int green = argb >> 8 & 255;
                int blue = argb & 255;

                int newRed = applyDithering(red, x, y, width, height, outputImage, 16);
                int newGreen = applyDithering(green, x, y, width, height, outputImage, 8);
                int newBlue = applyDithering(blue, x, y, width, height, outputImage, 0);

                int newARGB = alpha << 24 | newRed << 16 | newGreen << 8 | newBlue;
                outputImage.setRGB(x, y, newARGB);
            }
        }

        update(new FiltrationCompletedEvent(outputImage));
    }

    private int applyDithering(int oldColor, int x, int y, int width, int height, BufferedImage image, int shift) {
        int step = 16;
        switch (shift) {
            case 16:
                step = 255 / (filterModel.getInteger("red level"));
            case 8:
                step = 255 / (filterModel.getInteger("green level"));
            case 0:
                step = 255 / (filterModel.getInteger("blue level"));
        }

        int newColor = (oldColor + step / 2) / step * step; // С округлением

        int error = oldColor - newColor;
        int nextPixel;
        int nextColor;

        if (x + 1 < width) {
            nextPixel = image.getRGB(x + 1, y);
            nextColor = nextPixel >> shift & 255;
            nextColor += error * 7 / 16;
            nextColor = clamp(nextColor, 0, 255);
            image.setRGB(x + 1, y, nextPixel & ~(255 << shift) | nextColor << shift);
        }

        if (x - 1 >= 0 && y + 1 < height) {
            nextPixel = image.getRGB(x - 1, y + 1);
            nextColor = nextPixel >> shift & 255;
            nextColor += error * 3 / 16;
            nextColor = clamp(nextColor, 0, 255);
            image.setRGB(x - 1, y + 1, nextPixel & ~(255 << shift) | nextColor << shift);
        }

        if (y + 1 < height) {
            nextPixel = image.getRGB(x, y + 1);
            nextColor = nextPixel >> shift & 255;
            nextColor += error * 5 / 16;
            nextColor = clamp(nextColor, 0, 255);
            image.setRGB(x, y + 1, nextPixel & ~(255 << shift) | nextColor << shift);
        }

        if (x + 1 < width && y + 1 < height) {
            nextPixel = image.getRGB(x + 1, y + 1);
            nextColor = nextPixel >> shift & 255;
            nextColor += error * 1 / 16;
            nextColor = clamp(nextColor, 0, 255);
            image.setRGB(x + 1, y + 1, nextPixel & ~(255 << shift) | nextColor << shift);
        }

        return newColor;
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}