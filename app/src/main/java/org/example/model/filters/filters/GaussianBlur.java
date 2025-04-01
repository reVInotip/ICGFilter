package org.example.model.filters.filters;

import org.example.model.events.FiltrationCompletedEvent;
import org.example.model.filters.Filter;
import org.example.model.filters.FilterPrototype;
import org.example.model.filters.filterModels.ModelPrototype;

import java.awt.image.BufferedImage;

@Filter(descr = "Гауссово размытие", icon = "/utils/gaussian.png")
public class GaussianBlur extends FilterPrototype {
    private final int[] smallBlurMatrix = {
           0, 1, 0,
           1, 2, 1,
           0, 1, 0
    };

    private final double smallDivider = 6;

    private final int[] bigBlurMatrix = {
            1, 2, 3, 2, 1,
            2, 4, 5, 4, 2,
            3, 5, 6, 5, 3,
            2, 4, 5, 4, 2,
            1, 2, 3, 2, 1
    };

    private final double bigDivider = 74;

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

        int halfLenForRed = sizeForRedChannel / 2;
        int halfLenForGreen = sizeForGreenChannel / 2;
        int halfLenForBlue = sizeForBlueChannel / 2;

        double dividerForRed = sizeForRedChannel == 3 ? smallDivider : bigDivider;
        double dividerForGreen = sizeForGreenChannel == 3 ? smallDivider : bigDivider;
        double dividerForBlue = sizeForBlueChannel == 3 ? smallDivider : bigDivider;

        int[] redMatrix = sizeForRedChannel == 3 ? smallBlurMatrix : bigBlurMatrix;
        int[] greenMatrix = sizeForGreenChannel == 3 ? smallBlurMatrix : bigBlurMatrix;
        int[] blueMatrix = sizeForBlueChannel == 3 ? smallBlurMatrix : bigBlurMatrix;

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
                    int kernelValue = redMatrix[(ky + halfLenForRed) * sizeForRedChannel + (kx + halfLenForRed)];
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
                    int kernelValue = greenMatrix[(ky + halfLenForGreen) * sizeForGreenChannel + (kx + halfLenForGreen)];
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
                    int kernelValue = blueMatrix[(ky + halfLenForBlue) * sizeForBlueChannel + (kx + halfLenForBlue)];
                    sumB += (int) ((double) (b * kernelValue) / dividerForBlue);
                }
            }
        }

        sumR = Math.min(Math.max(sumR, 0), 255);
        sumG = Math.min(Math.max(sumG, 0), 255);
        sumB = Math.min(Math.max(sumB, 0), 255);
        return 255 << 24 | sumR << 16 | sumG << 8 | sumB;
    }
}