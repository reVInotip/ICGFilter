package org.example.model.filters.filters;

import org.example.model.events.FiltrationCompletedEvent;
import org.example.model.filters.Filter;
import org.example.model.filters.FilterPrototype;
import org.example.model.filters.filterModels.ModelPrototype;

import java.awt.image.BufferedImage;
@Filter(descr = "Дизеринг", icon = "")
public class OrderedDither extends FilterPrototype {
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

    public OrderedDither(ModelPrototype filterModel) {
        super(filterModel);
    }

    static private int transform(int intensity, int x, int y) {
        if (intensity > normalizationK * matrix[y * matrixWidth + x]) {
            return 255;
        } else {
            return 0;
        }
    }

    public void convert(BufferedImage image, BufferedImage result) {

        if (image == null) {
            throw new IllegalArgumentException("Image cannot be null");
        }

        int color, red, green, blue, alpha, x, y;
        for (int i = 0; i < image.getHeight(); ++i) {
            for (int j = 0; j < image.getWidth(); ++j) {
                x = j % matrixWidth;
                y = i % matrixHeight;

                color = image.getRGB(j, i);
                red = transform((color & 0xff0000) >> 16, x, y);
                green = transform((color & 0xff00) >> 8, x, y);
                blue = transform(color & 0xff, x, y);

                color = 0;
                color |= blue | (green << 8) | (red << 16) | (255 << 24);
                result.setRGB(j, i, color);
            }
        }

        update(new FiltrationCompletedEvent(result));
    }
}
