package org.example.model.filters.filters;

import org.example.model.events.FiltrationCompletedEvent;
import org.example.model.filters.Filter;
import org.example.model.filters.FilterPrototype;
import org.example.model.filters.filterModels.ModelPrototype;

import java.awt.image.BufferedImage;

@Filter(descr = "Гауссово размытие", icon = "/utils/gaussian.png")
public class GaussianBlur extends FilterPrototype {
    private final int[] kernel3x3 = {
            0, 1, 0,
            1, 2, 1,
            0, 1, 0
    };
    private final double divider3x3 = 6;

    private final int[] kernel5x5 = {
            1, 2, 3, 2, 1,
            2, 4, 5, 4, 2,
            3, 5, 6, 5, 3,
            2, 4, 5, 4, 2,
            1, 2, 3, 2, 1
    };
    private final double divider5x5 = 74;

    private final int[] kernel7x7 = {
            0, 0, 1, 2, 1, 0, 0,
            0, 2, 3, 5, 3, 2, 0,
            1, 3, 6, 8, 6, 3, 1,
            2, 5, 8, 10, 8, 5, 2,
            1, 3, 6, 8, 6, 3, 1,
            0, 2, 3, 5, 3, 2, 0,
            0, 0, 1, 2, 1, 0, 0
    };
    private final double divider7x7 = 140;

    private final int[] kernel9x9 = {
            0, 0, 1, 1, 2, 1, 1, 0, 0,
            0, 1, 2, 3, 4, 3, 2, 1, 0,
            1, 2, 4, 6, 7, 6, 4, 2, 1,
            1, 3, 6, 8, 10, 8, 6, 3, 1,
            2, 4, 7, 10, 12, 10, 7, 4, 2,
            1, 3, 6, 8, 10, 8, 6, 3, 1,
            1, 2, 4, 6, 7, 6, 4, 2, 1,
            0, 1, 2, 3, 4, 3, 2, 1, 0,
            0, 0, 1, 1, 2, 1, 1, 0, 0
    };
    private final double divider9x9 = 252;

    private final int[] kernel11x11 = {
            0, 0, 0, 1, 1, 2, 1, 1, 0, 0, 0,
            0, 0, 1, 2, 3, 4, 3, 2, 1, 0, 0,
            0, 1, 2, 4, 5, 6, 5, 4, 2, 1, 0,
            1, 2, 4, 6, 8, 9, 8, 6, 4, 2, 1,
            1, 3, 5, 8, 10, 11, 10, 8, 5, 3, 1,
            2, 4, 6, 9, 11, 12, 11, 9, 6, 4, 2,
            1, 3, 5, 8, 10, 11, 10, 8, 5, 3, 1,
            1, 2, 4, 6, 8, 9, 8, 6, 4, 2, 1,
            0, 1, 2, 4, 5, 6, 5, 4, 2, 1, 0,
            0, 0, 1, 2, 3, 4, 3, 2, 1, 0, 0,
            0, 0, 0, 1, 1, 2, 1, 1, 0, 0, 0
    };
    private final double divider11x11 = 384;

    public GaussianBlur(ModelPrototype filterModel) {
        super(filterModel);
    }

    @Override
    public void convert(BufferedImage image, BufferedImage result) {
        if (image == null) {
            throw new IllegalArgumentException("Image cannot be null");
        }

        int width = image.getWidth();
        int height = image.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int newColor = applyKernel(image, x, y);
                result.setRGB(x, y, newColor);
            }
        }

        update(new FiltrationCompletedEvent(result));
    }

    private int applyKernel(BufferedImage image, int x, int y) {
        int sizeForRedChannel = filterModel.getMatrixData("kernel size").getCurrSizeForRedChannel();
        int sizeForGreenChannel = filterModel.getMatrixData("kernel size").getCurrSizeForGreenChannel();
        int sizeForBlueChannel = filterModel.getMatrixData("kernel size").getCurrSizeForBlueChannel();

        int[] kernelForRed = getKernel(sizeForRedChannel);
        int[] kernelForGreen = getKernel(sizeForGreenChannel);
        int[] kernelForBlue = getKernel(sizeForBlueChannel);

        double dividerForRed = getDivider(sizeForRedChannel);
        double dividerForGreen = getDivider(sizeForGreenChannel);
        double dividerForBlue = getDivider(sizeForBlueChannel);

        int halfLenForRed = sizeForRedChannel / 2;
        int halfLenForGreen = sizeForGreenChannel / 2;
        int halfLenForBlue = sizeForBlueChannel / 2;

        int sumR = 0;
        int sumG = 0;
        int sumB = 0;

        for (int ky = -halfLenForRed; ky <= halfLenForRed; ky++) {
            for (int kx = -halfLenForRed; kx <= halfLenForRed; kx++) {
                int nx = x + kx;
                int ny = y + ky;
                if (nx >= 0 && ny >= 0 && nx < image.getWidth() && ny < image.getHeight()) {
                    int pixel = image.getRGB(nx, ny);
                    int r = pixel >> 16 & 255;
                    int kernelValue = kernelForRed[(ky + halfLenForRed) * sizeForRedChannel + (kx + halfLenForRed)];
                    sumR += (int) ((double) (r * kernelValue) / dividerForRed);
                }
            }
        }

        for (int ky = -halfLenForGreen; ky <= halfLenForGreen; ky++) {
            for (int kx = -halfLenForGreen; kx <= halfLenForGreen; kx++) {
                int nx = x + kx;
                int ny = y + ky;
                if (nx >= 0 && ny >= 0 && nx < image.getWidth() && ny < image.getHeight()) {
                    int pixel = image.getRGB(nx, ny);
                    int g = pixel >> 8 & 255;
                    int kernelValue = kernelForGreen[(ky + halfLenForGreen) * sizeForGreenChannel + (kx + halfLenForGreen)];
                    sumG += (int) ((double) (g * kernelValue) / dividerForGreen);
                }
            }
        }

        for (int ky = -halfLenForBlue; ky <= halfLenForBlue; ky++) {
            for (int kx = -halfLenForBlue; kx <= halfLenForBlue; kx++) {
                int nx = x + kx;
                int ny = y + ky;
                if (nx >= 0 && ny >= 0 && nx < image.getWidth() && ny < image.getHeight()) {
                    int pixel = image.getRGB(nx, ny);
                    int b = pixel & 255;
                    int kernelValue = kernelForBlue[(ky + halfLenForBlue) * sizeForBlueChannel + (kx + halfLenForBlue)];
                    sumB += (int) ((double) (b * kernelValue) / dividerForBlue);
                }
            }
        }

        sumR = Math.min(Math.max(sumR, 0), 255);
        sumG = Math.min(Math.max(sumG, 0), 255);
        sumB = Math.min(Math.max(sumB, 0), 255);
        return 255 << 24 | sumR << 16 | sumG << 8 | sumB;
    }

    private int[] getKernel(int size) {
        switch (size) {
            case 3: return kernel3x3;
            case 5: return kernel5x5;
            case 7: return kernel7x7;
            case 9: return kernel9x9;
            case 11: return kernel11x11;
            default: throw new IllegalArgumentException("Unsupported kernel size: " + size);
        }
    }

    private double getDivider(int size) {
        switch (size) {
            case 3: return divider3x3;
            case 5: return divider5x5;
            case 7: return divider7x7;
            case 9: return divider9x9;
            case 11: return divider11x11;
            default: throw new IllegalArgumentException("Unsupported kernel size: " + size);
        }
    }
}