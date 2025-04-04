package org.example.model.filters.filters;

import org.example.model.events.FiltrationCompletedEvent;
import org.example.model.filters.Filter;
import org.example.model.filters.FilterPrototype;
import org.example.model.filters.filterModels.ModelPrototype;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Filter(descr = "Гауссово размытие", icon = "/utils/gaussian.png")
public class GaussianBlur extends FilterPrototype {
    private final Map<Integer, double[]> kernelCache = new HashMap<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(3);

    public GaussianBlur(ModelPrototype filterModel) {
        super(filterModel);
    }

    @Override
    public void convert(BufferedImage image, BufferedImage result) {
        if (image == null) {
            throw new IllegalArgumentException("Image cannot be null");
        }

        int sizeR = filterModel.getMatrixData("kernel size").getCurrSizeForRedChannel();
        int sizeG = filterModel.getMatrixData("kernel size").getCurrSizeForGreenChannel();
        int sizeB = filterModel.getMatrixData("kernel size").getCurrSizeForBlueChannel();

        double[] kernelR = getCachedKernel(sizeR);
        double[] kernelG = getCachedKernel(sizeG);
        double[] kernelB = getCachedKernel(sizeB);

        int[][] redChannel = new int[image.getWidth()][image.getHeight()];
        int[][] greenChannel = new int[image.getWidth()][image.getHeight()];
        int[][] blueChannel = new int[image.getWidth()][image.getHeight()];

        executor.execute(() -> processChannel(image, redChannel, kernelR, sizeR, 16));
        executor.execute(() -> processChannel(image, greenChannel, kernelG, sizeG, 8));
        executor.execute(() -> processChannel(image, blueChannel, kernelB, sizeB, 0));

        try {
            executor.shutdown();
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int r = Math.min(Math.max(redChannel[x][y], 0), 255);
                int g = Math.min(Math.max(greenChannel[x][y], 0), 255);
                int b = Math.min(Math.max(blueChannel[x][y], 0), 255);
                result.setRGB(x, y, (0xFF << 24) | (r << 16) | (g << 8) | b);
            }
        }

        update(new FiltrationCompletedEvent(result));
    }

    private void processChannel(BufferedImage image, int[][] channel, double[] kernel, int size, int shift) {
        int radius = size / 2;

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                double sum = 0;

                for (int ky = -radius; ky <= radius; ky++) {
                    for (int kx = -radius; kx <= radius; kx++) {
                        int nx = x + kx;
                        int ny = y + ky;

                        if (nx < 0) nx = 0;
                        if (ny < 0) ny = 0;
                        if (nx >= image.getWidth()) nx = image.getWidth() - 1;
                        if (ny >= image.getHeight()) ny = image.getHeight() - 1;

                        int pixel = image.getRGB(nx, ny);
                        double weight = kernel[(ky + radius) * size + (kx + radius)];
                        sum += ((pixel >> shift) & 0xFF) * weight;
                    }
                }
                channel[x][y] = (int) Math.round(sum);
            }
        }
    }

    private double[] getCachedKernel(int size) {
        if (!kernelCache.containsKey(size)) {
            kernelCache.put(size, generateGaussianKernel(size));
        }
        return kernelCache.get(size);
    }

    private double[] generateGaussianKernel(int size) {
        double sigma = size / 3.0;
        double[] kernel = new double[size * size];
        double sum = 0.0;
        int radius = size / 2;

        for (int y = -radius; y <= radius; y++) {
            for (int x = -radius; x <= radius; x++) {
                double value = Math.exp(-(x * x + y * y) / (2 * sigma * sigma));
                kernel[(y + radius) * size + (x + radius)] = value;
                sum += value;
            }
        }

        for (int i = 0; i < kernel.length; i++) {
            kernel[i] /= sum;
        }

        return kernel;
    }
}