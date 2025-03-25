package org.example.model.filters.filters;

import org.example.model.events.FiltrationCompletedEvent;
import org.example.model.filters.Filter;
import org.example.model.filters.FilterPrototype;
import org.example.model.filters.filterModels.ModelPrototype;

import java.awt.image.BufferedImage;

@Filter(descr = "эфект теснения", icon = "/utils/embossing.png")
public class Embossing extends FilterPrototype {
    private static final int[][] EMBOSS_KERNEL = new int[][]{{-1, -1, 0}, {-1, 0, 1}, {0, 1, 1}};

    public Embossing(ModelPrototype filterModel) {
        super(filterModel);
    }

    public void convert(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage result = new BufferedImage(width, height, 2);

        for(int y = 1; y < height - 1; ++y) {
            for(int x = 1; x < width - 1; ++x) {
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

        int ky;
        int kx;
        int pixel;
        for(ky = -1; ky <= 1; ++ky) {
            for(kx = -1; kx <= 1; ++kx) {
                pixel = image.getRGB(x + kx, y + ky);
                int r = pixel >> 16 & 255;
                int g = pixel >> 8 & 255;
                int b = pixel & 255;
                int kernelValue = EMBOSS_KERNEL[ky + 1][kx + 1];
                sumR += r * kernelValue;
                sumG += g * kernelValue;
                sumB += b * kernelValue;
            }
        }

        int Red = clamp(sumR + 128);
        int Green = clamp(sumG + 128);
        int Blue = clamp(sumB + 128);
        return -16777216 | Red << 16 | Green << 8 | Blue;
    }

    private static int clamp(int value) {
        return Math.min(Math.max(value, 0), 255);
    }
}