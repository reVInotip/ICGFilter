package org.example.model.filters.filters;

import org.example.model.events.FiltrationCompletedEvent;
import org.example.model.filters.Filter;
import org.example.model.filters.FilterPrototype;
import org.example.model.filters.filterModels.ModelPrototype;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Filter(descr = "Упорядоченный дизеринг", icon = "/utils/ordered_dithering.png")
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

    private int transform(int intensity, int x, int y, ArrayList<Integer> palette, Integer[] matrix, int matrixSize, int level) {
        double matrixNormalizer = 1.0 / (matrixSize * matrixSize);
        double threshold = matrix[y % matrixSize * matrixSize + x % matrixSize] * matrixNormalizer;

        double linearColor = intensity / 255.0;
        int levels = level + 1;

        int quantized = (int)(linearColor * (levels - 1) + (threshold - 0.5 * matrixNormalizer));
        quantized = Math.min(quantized, levels - 1);

        double srgb = quantized / (double)(levels - 1);
        return clamp((int)(srgb * 255), 0, 255);
    }

    private int findSuitableMatrix(int quantizationNumber) {
        if (256.0 / quantizationNumber >= 16 * 16) {
            return 4;
        } else if (256.0 / quantizationNumber >= 8 * 8) {
            return 3;
        } else if (256.0 / quantizationNumber >= 4 * 4) {
            return 2;
        } else {
            return 1;
        }
    }

    private ArrayList<Integer> createPalette(int quantizationNumber) {
        int levels = quantizationNumber + 1;
        ArrayList<Integer> palette = new ArrayList<>();
        for (int i = 0; i < levels; i++) {
            int value = (int)((i / (double)(levels - 1)) * 255);
            palette.add(value);
        }
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
                red = transform((color & 0xff0000) >> 16, x, y, paletteForRed, redMatrix, redMatrixSize, redQuantizationNumber);

                x = j % greenMatrixSize;
                y = i % greenMatrixSize;
                green = transform((color & 0xff00) >> 8, x, y, paletteForGreen, greenMatrix, greenMatrixSize, greenQuantizationNumber);

                x = j % blueMatrixSize;
                y = i % blueMatrixSize;
                blue = transform(color & 0xff, x, y, paletteForBlue, blueMatrix, blueMatrixSize, blueQuantizationNumber);

                alpha = (color & 0xff000000) >> 24;

                color = 0;
                color |= blue | (green << 8) | (red << 16) | (alpha << 24);
                result.setRGB(j, i, color);
            }
        }

        update(new FiltrationCompletedEvent(result));
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
//package org.example.model.filters.filters;
//
//import org.example.model.events.FiltrationCompletedEvent;
//import org.example.model.filters.Filter;
//import org.example.model.filters.FilterPrototype;
//import org.example.model.filters.filterModels.ModelPrototype;
//
//import java.awt.image.BufferedImage;
//import java.util.ArrayList;
//import java.util.List;
//
//@Filter(descr = "Упорядоченный дизеринг", icon = "/utils/ordered_dithering.png")
//public class OrderedDither extends FilterPrototype {
//    private final RGBPalette palette;
//    private final DitheringMatrix rMatrix;
//    private final DitheringMatrix gMatrix;
//    private final DitheringMatrix bMatrix;
//    private final int rThreshold;
//    private final int gThreshold;
//    private final int bThreshold;
//
//    public OrderedDither(ModelPrototype filterModel) {
//        super(filterModel);
//
//        int r = filterModel.getInteger("red quantization number");
//        int g = filterModel.getInteger("green quantization number");
//        int b = filterModel.getInteger("blue quantization number");
//
//        this.palette = new RGBPalette(r, g, b);
//        this.rMatrix = new DitheringMatrix(32 - Integer.numberOfLeadingZeros(r));
//        this.gMatrix = new DitheringMatrix(32 - Integer.numberOfLeadingZeros(g));
//        this.bMatrix = new DitheringMatrix(32 - Integer.numberOfLeadingZeros(b));
//        this.rThreshold = 256 / (r - 1);
//        this.gThreshold = 256 / (g - 1);
//        this.bThreshold = 256 / (b - 1);
//    }
//
//    private static class DitheringMatrix {
//        public final int[][] matrix;
//        public final int offset;
//
//        public DitheringMatrix(int size) {
//            int len = 1 << size;
//            this.offset = len * len;
//            this.matrix = new int[len][len];
//            int move = (Integer.BYTES << 3) - size;
//
//            for (int i = 0; i < len; i++) {
//                for (int j = 0; j < len; j++) {
//                    int ii = Integer.reverse(i) >>> move;
//                    int ij = Integer.reverse(j) >>> move;
//                    matrix[i][j] = interleave(ii ^ ij, ii);
//                }
//            }
//        }
//
//        private static int interleave(int a, int b) {
//            return (spaceOut(a) << 1) | spaceOut(b);
//        }
//
//        private static int spaceOut(int x) {
//            x = (x | (x << 16)) & 0x0000FFFF;
//            x = (x | (x << 8)) & 0x00FF00FF;
//            x = (x | (x << 4)) & 0x0F0F0F0F;
//            x = (x | (x << 2)) & 0x33333333;
//            x = (x | (x << 1)) & 0x55555555;
//            return x;
//        }
//    }
//
//    private static class RGBPalette {
//        private final int rColors;
//        private final int gColors;
//        private final int bColors;
//
//        public RGBPalette(int r, int g, int b) {
//            this.rColors = r;
//            this.gColors = g;
//            this.bColors = b;
//        }
//
//        public int findCloset(int r, int g, int b) {
//            int rStep = 255 / (rColors - 1);
//            int gStep = 255 / (gColors - 1);
//            int bStep = 255 / (bColors - 1);
//
//            int newR = Math.round((float) r / rStep) * rStep;
//            int newG = Math.round((float) g / gStep) * gStep;
//            int newB = Math.round((float) b / bStep) * bStep;
//
//            newR = Math.min(255, Math.max(0, newR));
//            newG = Math.min(255, Math.max(0, newG));
//            newB = Math.min(255, Math.max(0, newB));
//
//            return (newR << 16) | (newG << 8) | newB;
//        }
//    }
//
//    public void convert(BufferedImage image, BufferedImage result) {
//        if (image == null) {
//            throw new IllegalArgumentException("Image cannot be null");
//        }
//
//        int[][] rm = rMatrix.matrix;
//        int rqs = rMatrix.offset;
//        int rs = rMatrix.matrix.length;
//        int[][] gm = gMatrix.matrix;
//        int gqs = gMatrix.offset;
//        int gs = gMatrix.matrix.length;
//        int[][] bm = bMatrix.matrix;
//        int bqs = bMatrix.offset;
//        int bs = bMatrix.matrix.length;
//
//        for (int y = 0; y < image.getHeight(); y++) {
//            for (int x = 0; x < image.getWidth(); x++) {
//                int rgb = image.getRGB(x, y);
//
//                int rv = rm[x % rs][y % rs] - rqs / 2;
//                int gv = gm[x % gs][y % gs] - gqs / 2;
//                int bv = bm[x % bs][y % bs] - bqs / 2;
//
//                int r = (((rgb >> 16) & 0xff) + (rv * rThreshold) / rqs);
//                int g = (((rgb >> 8) & 0xff) + (gv * gThreshold) / gqs);
//                int b = ((rgb & 0xff) + (bv * bThreshold) / bqs);
//
//                rgb = palette.findCloset(r, g, b);
//                result.setRGB(x, y, rgb);
//            }
//        }
//
//        update(new FiltrationCompletedEvent(result));
//    }
//}