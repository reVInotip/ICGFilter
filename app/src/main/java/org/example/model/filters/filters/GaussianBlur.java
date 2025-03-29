package org.example.model.filters.filters;

import org.example.model.events.FiltrationCompletedEvent;
import org.example.model.filters.Filter;
import org.example.model.filters.FilterPrototype;
import org.example.model.filters.filterModels.ModelPrototype;

import java.awt.image.BufferedImage;

@Filter(descr = "Гауссово размытие", icon = "/utils/gaussian.png")
public class GaussianBlur extends FilterPrototype {

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

        int matrixLen = filterModel.getMatrix("kernel").getWidth();
        int halfLen = matrixLen / 2;
        Double divider = filterModel.getDouble("divider");

        int sumR = 0;
        int sumG = 0;
        int sumB = 0;

        for (int ky = -halfLen; ky <= halfLen; ky++) {
            for (int kx = -halfLen; kx <= halfLen; kx++) {
                int nx = x + kx;
                int ny = y + ky;
                if (nx >= 0 && ny >= 0 && nx < image.getWidth() && ny < image.getHeight()) {
                    int pixel = image.getRGB(nx, ny);
                    int r = pixel >> 16 & 255;
                    int g = pixel >> 8 & 255;
                    int b = pixel & 255;
                    int kernelValue = filterModel.getMatrix("kernel").get(kx + halfLen, ky + halfLen);
                    sumR += (int) ((double) (r * kernelValue) / divider);
                    sumG += (int) ((double) (g * kernelValue) / divider);
                    sumB += (int) ((double) (b * kernelValue) / divider);
                }
            }
        }

        sumR = Math.min(Math.max(sumR, 0), 255);
        sumG = Math.min(Math.max(sumG, 0), 255);
        sumB = Math.min(Math.max(sumB, 0), 255);
        return 255 << 24 | sumR << 16 | sumG << 8 | sumB;
    }
}