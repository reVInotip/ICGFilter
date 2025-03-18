package model.filters.filters;

import model.filters.Filter;
import model.filters.IFiltersModel;

import java.awt.image.BufferedImage;

@Filter(name = "inversion")
public class Inversion extends IFiltersModel {
    @Override
    public void convert(BufferedImage image) {
        int color, red, green, blue;
        for (int i = 0; i < image.getHeight(); ++i) {
            for (int j = 0; j < image.getWidth(); ++j) {
                color = image.getRGB(j, i);
                red = 255 - ((color & 0xff0000) >> 16);
                green = 255 - ((color & 0xff00) >> 8);
                blue = 255 - (color & 0xff);

                color = 0;
                color |= blue | (green << 8) | (red << 16);
                image.setRGB(j, i, color);
            }
        }
    }
}
