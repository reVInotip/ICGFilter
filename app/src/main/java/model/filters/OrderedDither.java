package model.filters;

import java.awt.image.BufferedImage;

public class OrderedDither {
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

    static private int transform(int intensity, int x, int y) {
        if (intensity > normalizationK * matrix[y * matrixWidth + x]) {
            return 255;
        } else {
            return 0;
        }
    }

    static public void apply(BufferedImage image) {
        int color, red, green, blue, alpha, x, y;
        for (int i = 0; i < image.getHeight(); ++i) {
            for (int j = 0; j < image.getWidth(); ++j) {
                x = j % matrixWidth;
                y = i % matrixHeight;

                color = image.getRGB(j, i);
                red = transform((color & 0xff0000) >> 16, x, y);
                green = transform((color & 0xff00) >> 8, x, y);
                blue = transform(color & 0xff, x, y);
                alpha = transform((color & 0xff000000) >> 24, x, y);

                color = 0;
                color |= blue | (green << 8) | (red << 16) | (alpha << 24);
                image.setRGB(j, i, color);
            }
        }
    }
}
