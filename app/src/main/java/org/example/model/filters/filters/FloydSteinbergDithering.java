package org.example.model.filters.filters;

import org.example.model.events.FiltrationCompletedEvent;
import org.example.model.filters.Filter;
import org.example.model.filters.FilterPrototype;
import org.example.model.filters.filterModels.ModelPrototype;

import java.awt.image.BufferedImage;

@Filter(descr = "Дизеринг Флойда-Стейнберга", icon = "/utils/dithering.png")
public class FloydSteinbergDithering extends FilterPrototype {
    public FloydSteinbergDithering(ModelPrototype filterModel) {
        super(filterModel);
    }

    @Override
    public void convert(BufferedImage image) {
        if (image == null) {
            throw new IllegalArgumentException("Image cannot be null");
        }

        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // Создаем копию изображения для работы
        BufferedImage workImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                workImage.setRGB(x, y, image.getRGB(x, y));
            }
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = workImage.getRGB(x, y);
                int alpha = argb >> 24 & 255;
                int red = argb >> 16 & 255;
                int green = argb >> 8 & 255;
                int blue = argb & 255;

                int newRed = applyDithering(red, x, y, width, height, workImage, 16);
                int newGreen = applyDithering(green, x, y, width, height, workImage, 8);
                int newBlue = applyDithering(blue, x, y, width, height, workImage, 0);

                int newARGB = alpha << 24 | newRed << 16 | newGreen << 8 | newBlue;
                outputImage.setRGB(x, y, newARGB);
            }
        }

        update(new FiltrationCompletedEvent(outputImage));
    }

    private int applyDithering(int oldColor, int x, int y, int width, int height, BufferedImage image, int shift) {
        int newColor = oldColor < 85 ? 0 : (oldColor < 170 ? 128 : 255);
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