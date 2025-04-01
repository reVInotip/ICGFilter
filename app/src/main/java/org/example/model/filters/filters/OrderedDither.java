package org.example.model.filters.filters;

import org.example.model.events.FiltrationCompletedEvent;
import org.example.model.filters.Filter;
import org.example.model.filters.FilterPrototype;
import org.example.model.filters.filterModels.ModelPrototype;
import org.example.model.filters.filters.types.Pair;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

@Filter(descr = "Дизеринг", icon = "")
public class OrderedDither extends FilterPrototype {
    static final private int[] matrix = {
            0, 32, 8, 40, 2, 34, 10, 42,
            48, 16, 56, 24, 50, 18, 58, 26,
            12, 44, 4, 36, 14, 46, 6, 38,
            60, 28, 52, 20, 62, 30, 54, 22,
            3, 35, 11, 43, 1, 33, 9, 41,
            51, 19, 59, 27, 49, 17, 57, 25,
            15, 47, 7, 39, 13, 45, 5, 37,
            63, 31, 55, 23, 61, 29, 53, 21
    };
    static final private int normalizationK = 4;
    static final private int matrixHeight = 8;
    static final private int matrixWidth = 8;

    public OrderedDither(ModelPrototype filterModel) {
        super(filterModel);
    }

    static private int transform(int intensity, int x, int y) {
        if (intensity > normalizationK * matrix[y * matrixWidth + x]) {
            return 255;
        } else {
            return 0;
        }
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

    private ArrayList<Pair<Integer, Integer>> createRanges(int quantizationNumber) {
        int step = (int) Math.round(255.0 / quantizationNumber);

        ArrayList<Pair<Integer, Integer>> ranges = new ArrayList<>();

        ranges.add(new Pair<>(Integer.MIN_VALUE, 0));

        int i = step;
        for (; i <= 255 - step; i += step) {
            ranges.add(new Pair<>(i, i + step));
        }

        ranges.add(new Pair<>(i + step, Integer.MAX_VALUE));

        return ranges;
    }

    public void convert(BufferedImage image, BufferedImage result) {
        if (image == null) {
            throw new IllegalArgumentException("Image cannot be null");
        }

        int redQuantizationNumber = filterModel.getInteger("red quantization number");
        int greenQuantizationNumber = filterModel.getInteger("green quantization number");
        int blueQuantizationNumber = filterModel.getInteger("blue quantization number");

        ArrayList<Integer> paletteForRed = createPalette(redQuantizationNumber);
        ArrayList<Integer> paletteForGreen = createPalette(greenQuantizationNumber);
        ArrayList<Integer> paletteForBlue = createPalette(blueQuantizationNumber);

        ArrayList<Pair<Integer, Integer>> rangesForRed = createRanges(redQuantizationNumber);
        ArrayList<Pair<Integer, Integer>> rangesForGreen = createRanges(greenQuantizationNumber);
        ArrayList<Pair<Integer, Integer>> rangesForBlue = createRanges(blueQuantizationNumber);

        int color, red, green, blue, x, y;
        for (int i = 0; i < image.getHeight(); ++i) {
            for (int j = 0; j < image.getWidth(); ++j) {
                x = j % matrixWidth;
                y = i % matrixHeight;

                color = image.getRGB(j, i);
                red = transform((color & 0xff0000) >> 16, x, y);
                green = transform((color & 0xff00) >> 8, x, y);
                blue = transform(color & 0xff, x, y);

                color = 0;
                color |= blue | (green << 8) | (red << 16) | (255 << 24);
                result.setRGB(j, i, color);
            }
        }

        update(new FiltrationCompletedEvent(result));
    }
}
