package org.example.model.filters.filters;

import org.example.model.events.FiltrationCompletedEvent;
import org.example.model.filters.Filter;
import org.example.model.filters.FilterPrototype;
import org.example.model.filters.filterModels.ModelPrototype;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Filter(descr = "Дизеринг", icon = "")
public class OrderedDither extends FilterPrototype {
    final private List<Integer[]> matrices = new ArrayList<>();
    final private List<Integer> sizes = new ArrayList<>();

    public OrderedDither(ModelPrototype filterModel) {
        super(filterModel);

        matrices.add(new Integer[]{0});
        sizes.add(1);

        int baseSize = 2;
        for (int i = 0; i < 4; ++i) {
            Integer[] newMatrix = new Integer[baseSize * baseSize];
            Integer[] prevMatrix = matrices.getLast();
            int prY;

            for (int y = 0; y < baseSize / 2; ++y) {
                prY = y % (baseSize / 2);
                for (int x = 0; x < baseSize / 2; ++x) {
                    newMatrix[y * baseSize + x] = 4 * prevMatrix[prY * (baseSize / 2) + x % (baseSize / 2)];
                }
                for (int x = baseSize / 2; x < baseSize; ++x) {
                    newMatrix[y * baseSize + x] = 4 * prevMatrix[prY * (baseSize / 2) + x % (baseSize / 2)] + 2;
                }
            }

            for (int y = baseSize / 2; y < baseSize; ++y) {
                prY = y % (baseSize / 2);
                for (int x = 0; x < baseSize / 2; ++x) {
                    newMatrix[y * baseSize + x] = 4 * prevMatrix[prY * (baseSize / 2) + x % (baseSize / 2)] + 3;
                }
                for (int x = baseSize / 2; x < baseSize; ++x) {
                    newMatrix[y * baseSize + x] = 4 * prevMatrix[prY * (baseSize / 2) + x % (baseSize / 2)] + 1;
                }
            }

            matrices.add(newMatrix);
            sizes.add(baseSize);

            baseSize *= 2;
        }
    }

    private int transform(int intensity, int x, int y, ArrayList<Integer> palette, Integer[] matrix, int matrixSize) {
        double matrixValue = ((double) matrix[y * matrixSize + x]) / (matrixSize * matrixSize);
        double updatedIntensity = intensity + matrixValue * 256 - 128;

        int color = 0;
        int distance = Integer.MAX_VALUE;

        for (Integer integer : palette) {
            if (Math.abs(integer - updatedIntensity) < distance) {
                distance = (int) Math.abs(integer - updatedIntensity);
                color = integer;
            }
        }

        return color;
    }

    private int findSuitableMatrix(int quantizationNumber) {
        if (quantizationNumber >= 2 && quantizationNumber < 30) {
            return 4;
        } else if (quantizationNumber >= 30 && quantizationNumber < 60) {
            return 3;
        } else if (quantizationNumber >= 60 && quantizationNumber < 90) {
            return 2;
        } else {
            return 1;
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

        int suitRedMatrixIndex = findSuitableMatrix(redQuantizationNumber);
        int suitBlueMatrixIndex = findSuitableMatrix(blueQuantizationNumber);
        int suitGreenMatrixIndex = findSuitableMatrix(greenQuantizationNumber);

        Integer[] redMatrix = matrices.get(suitRedMatrixIndex);
        Integer[] blueMatrix = matrices.get(suitBlueMatrixIndex);
        Integer[] greenMatrix = matrices.get(suitGreenMatrixIndex);

        int redMatrixSize = sizes.get(suitRedMatrixIndex);
        int blueMatrixSize = sizes.get(suitBlueMatrixIndex);
        int greenMatrixSize = sizes.get(suitGreenMatrixIndex);

        int color, red, green, blue, alpha, x, y;
        for (int i = 0; i < image.getHeight(); ++i) {
            for (int j = 0; j < image.getWidth(); ++j) {
                color = image.getRGB(j, i);

                x = j % redMatrixSize;
                y = i % redMatrixSize;
                red = transform((color & 0xff0000) >> 16, x, y, paletteForRed, redMatrix, redMatrixSize);

                x = j % greenMatrixSize;
                y = i % greenMatrixSize;
                green = transform((color & 0xff00) >> 8, x, y, paletteForGreen, greenMatrix, greenMatrixSize);

                x = j % blueMatrixSize;
                y = i % blueMatrixSize;
                blue = transform(color & 0xff, x, y, paletteForBlue, blueMatrix, blueMatrixSize);

                alpha = (color & 0xff000000) >> 24;

                color = 0;
                color |= blue | (green << 8) | (red << 16) | (alpha << 24);
                result.setRGB(j, i, color);
            }
        }

        update(new FiltrationCompletedEvent(result));
    }
}
