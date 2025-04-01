package org.example.model.filters.filters;

import org.example.model.events.FiltrationCompletedEvent;
import org.example.model.filters.Filter;
import org.example.model.filters.FilterPrototype;
import org.example.model.filters.filterModels.ModelPrototype;

import java.awt.image.BufferedImage;

@Filter(descr = "Фильтр резкости", icon = "/utils/sharpness.png")
public class SharpnessFilter extends FilterPrototype {
    private static final int[][] SHARPNESS_KERNEL = {
            {0, -1, 0},
            {-1, 5, -1},
            {0, -1, 0}
    };

    public SharpnessFilter(ModelPrototype filterModel) {
        super(filterModel);
    }

    @Override
    public void convert(BufferedImage image, BufferedImage result) {
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
        int sumR = 0;
        int sumG = 0;
        int sumB = 0;

        for (int ky = -1; ky <= 1; ky++) {
            for (int kx = -1; kx <= 1; kx++) {
                int nx = x + kx;
                int ny = y + ky;
                if (nx >= 0 && ny >= 0 && nx < image.getWidth() && ny < image.getHeight()) {
                    int pixel = image.getRGB(nx, ny);
                    int r = pixel >> 16 & 255;
                    int g = pixel >> 8 & 255;
                    int b = pixel & 255;
                    int kernelValue = SHARPNESS_KERNEL[ky + 1][kx + 1];
                    sumR += r * kernelValue;
                    sumG += g * kernelValue;
                    sumB += b * kernelValue;
                }
            }
        }

        sumR = clamp(sumR, 0, 255);
        sumG = clamp(sumG, 0, 255);
        sumB = clamp(sumB, 0, 255);
        int alpha = image.getRGB(x, y) >> 24 & 255;
        return alpha << 24 | sumR << 16 | sumG << 8 | sumB;
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}