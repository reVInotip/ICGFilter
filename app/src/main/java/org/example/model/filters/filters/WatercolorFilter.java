package org.example.model.filters.filters;

import org.example.model.events.FiltrationCompletedEvent;
import org.example.model.filters.Filter;
import org.example.model.filters.FilterPrototype;
import org.example.model.filters.filterModels.ModelPrototype;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Filter(descr = "Акварельный фильтр", icon = "/utils/watercolor.png")
public class WatercolorFilter extends FilterPrototype {
    private static final int[][] SHARPNESS_KERNEL = {
            {0, -1, 0},
            {-1, 5, -1},
            {0, -1, 0}
    };
    private static final int WINDOW_SIZE = 5;

    public WatercolorFilter(ModelPrototype filterModel) {
        super(filterModel);
    }

    @Override
    public void convert(BufferedImage image, BufferedImage result) {
        int width = image.getWidth();
        int height = image.getHeight();
        int halfWindow = WINDOW_SIZE / 2;

        BufferedImage preResult = new BufferedImage(width, height, 2);


        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                List<Integer> redValues = new ArrayList<>();
                List<Integer> greenValues = new ArrayList<>();
                List<Integer> blueValues = new ArrayList<>();

                for (int ky = -halfWindow; ky <= halfWindow; ky++) {
                    for (int kx = -halfWindow; kx <= halfWindow; kx++) {
                        int nx = x + kx;
                        int ny = y + ky;
                        if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                            int pixel = image.getRGB(nx, ny);
                            redValues.add(pixel >> 16 & 255);
                            greenValues.add(pixel >> 8 & 255);
                            blueValues.add(pixel & 255);
                        }
                    }
                }

                Collections.sort(redValues);
                Collections.sort(greenValues);
                Collections.sort(blueValues);

                int medianRed = redValues.get(redValues.size() / 2);
                int medianGreen = greenValues.get(greenValues.size() / 2);
                int medianBlue = blueValues.get(blueValues.size() / 2);

                int newPixel = medianRed << 16 | medianGreen << 8 | medianBlue;
                preResult.setRGB(x, y, newPixel);
            }
        }

        // Применяем фильтр резкости к результату
        for(int y = 0; y < height; ++y) {
            for(int x = 0; x < width; ++x) {
                int newColor = applyKernel(preResult, x, y);
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
        return 255 << 24 | sumR << 16 | sumG << 8 | sumB;
    }
    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}