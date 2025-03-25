package org.example.model.filters.filters;

import org.example.model.events.FiltrationCompletedEvent;
import org.example.model.filters.Filter;
import org.example.model.filters.FilterPrototype;

import java.awt.image.BufferedImage;

@Filter(descr = "инверсия", icon = "/utils/inversion.png")
public class Inversion extends FilterPrototype {
    @Override
    public void convert(BufferedImage image) {
        if (image == null) {
            throw new IllegalArgumentException("Image cannot be null");
        }

        int color, red, green, blue;
        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < image.getHeight(); ++i) {
            for (int j = 0; j < image.getWidth(); ++j) {
                color = image.getRGB(j, i);
                red = 255 - ((color >> 16) & 0xff);
                green = 255 - ((color >> 8) & 0xff);
                blue = 255 - (color & 0xff);

                color = (red << 16) | (green << 8) | blue;
                result.setRGB(j, i, color);
            }
        }

        update(new FiltrationCompletedEvent(result));
    }
}