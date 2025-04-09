package org.example.model.filters.filters;

import org.example.model.events.FiltrationCompletedEvent;
import org.example.model.filters.Filter;
import org.example.model.filters.FilterPrototype;
import org.example.model.filters.filterModels.ModelPrototype;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

@Filter(descr = "Дизеринг Флойда-Стейнберга 2", icon = "/utils/floydSteinbergDithering.png")
public class FloydSteinbergDithering2 extends FilterPrototype {
    public FloydSteinbergDithering2(ModelPrototype filterModel) {
        super(filterModel);
    }

    private int getX(int x, int width) {
        if (x < 0) {
            return -x;
        } else if (x >= width) {
            return 2 * width - x - 1;
        }

        return x;
    }

    private int getY(int y, int height) {
        if (y < 0) {
            return -y;
        } else if (y >= height) {
            return 2 * height - y - 1;
        }

        return y;
    }

    private void moveError(int x, int y, int width, int height, int error, BufferedImage image, int shift) {
        int color = image.getRGB(getX(x + 1, width), y);
        int intensity = truncate(((color >> shift) & 0xff) + 7 * error / 16);

        image.setRGB(getX(x + 1, width), y, (color & ~(0xff << shift)) | (intensity << shift));

        color = image.getRGB(getX(x - 1, width), getY(y + 1, height));
        intensity = truncate(((color >> shift) & 0xff) + 3 * error / 16);

        image.setRGB(getX(x - 1, width), getY(y + 1, height), (color & ~(0xff << shift)) | (intensity << shift));

        color = image.getRGB(x, getY(y + 1, height));
        intensity = truncate(((color >> shift) & 0xff) + 5 * error / 16);

        image.setRGB(x, getY(y + 1, height), (color & ~(0xff << shift)) | (intensity << shift));

        color = image.getRGB(getX(x + 1, width), getY(y + 1, height));
        intensity = truncate(((color >> shift) & 0xff) + error / 16);

        image.setRGB(getX(x + 1, width), getY(y + 1, height), (color & ~(0xff << shift)) | (intensity << shift));
    }

    private int truncate(int value) {
        return Math.max(0, Math.min(255, value));
    }

    private ArrayList<Integer> createPalette(int quantizationNumber) {
        int step = (int) Math.round(255.0 / quantizationNumber);

        ArrayList<Integer> palette = new ArrayList<>();
        for (int i = 0; i <= 255 - step; i += step) {
            palette.add(i);
        }

        palette.add(255);

        return palette;
    }

    private int getNearestIntensity(int intensity, ArrayList<Integer> palette) {
        int color = 0;
        int distance = Integer.MAX_VALUE;

        for (Integer nearestIntensity : palette) {
            if (Math.abs(nearestIntensity - intensity) < distance) {
                distance = (int) Math.abs(nearestIntensity - intensity);
                color = nearestIntensity;
            }
        }

        return color;
    }

    @Override
    public void convert(BufferedImage image, BufferedImage result) {
        if (image == null) {
            throw new IllegalArgumentException("invalid image");
        }

        int redQuantizationNumber = filterModel.getInteger("red quantization number");
        int greenQuantizationNumber = filterModel.getInteger("green quantization number");
        int blueQuantizationNumber = filterModel.getInteger("blue quantization number");

        ArrayList<Integer> paletteForRed = createPalette(redQuantizationNumber);
        ArrayList<Integer> paletteForGreen = createPalette(greenQuantizationNumber);
        ArrayList<Integer> paletteForBlue = createPalette(blueQuantizationNumber);

        int width = image.getWidth();
        int height = image.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                result.setRGB(x, y, image.getRGB(x, y));
            }
        }

        int color, red, green, blue, alpha;
        int error = 0, newRed, newGreen, newBlue;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                color = result.getRGB(x, y);

                red = (color & 0xff0000) >> 16;
                green = (color & 0xff00) >> 8;
                blue = color & 0xff;
                alpha = (color & 0xff000000) >> 24;

                newRed = getNearestIntensity(red, paletteForRed);
                newBlue = getNearestIntensity(blue, paletteForBlue);
                newGreen = getNearestIntensity(green, paletteForGreen);

                result.setRGB(x, y, alpha << 24 | newRed << 16 | newGreen << 8 | newBlue);

                error = red - newRed;
                moveError(x, y, width, height, error, result, 16);

                error = green - newGreen;
                moveError(x, y, width, height, error, result, 8);

                error = blue - newBlue;
                moveError(x, y, width, height, error, result, 0);
            }
        }

        update(new FiltrationCompletedEvent(result));
    }
}
