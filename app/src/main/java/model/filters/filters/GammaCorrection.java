package model.filters.filters;

import java.awt.image.BufferedImage;

public class GammaCorrection {
    static private double gamma = 2.2;

    static public void apply(BufferedImage image) {
        int color, red, green, blue;
        for (int i = 0; i < image.getHeight(); ++i) {
            for (int j = 0; j < image.getWidth(); ++j) {
                color = image.getRGB(j, i);
                red = (int) (255 * Math.pow(((color & 0xff0000) >> 16) / 255.0, 1 / gamma));
                green = (int) (255 * Math.pow(((color & 0xff00) >> 8) / 255.0, 1 / gamma));
                blue = (int) (255 * Math.pow((color & 0xff) / 255.0, 1 / gamma));

                color = blue | (green << 8) | (red << 16);
                image.setRGB(j, i, color);
            }
        }
    }
}
