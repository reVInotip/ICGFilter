package org.example.model.filters.filters;

import org.example.model.events.FiltrationCompletedEvent;
import org.example.model.filters.Filter;
import org.example.model.filters.FilterPrototype;
import org.example.model.filters.filterModels.ModelPrototype;

import java.awt.image.BufferedImage;

@Filter(descr = "инверсия", icon = "/utils/inversion.png")
public class Inversion extends FilterPrototype {
    public Inversion(ModelPrototype filterModel) {
        super(filterModel);
    }

    @Override
    public void convert(BufferedImage image, BufferedImage result) {
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int color = image.getRGB(x, y);

                int alpha = (color >> 24) & 0xff;
                int red   = 255 - ((color >> 16) & 0xff);
                int green = 255 - ((color >> 8) & 0xff);
                int blue  = 255 - (color & 0xff);

                int invertedColor = (alpha << 24) | (red << 16) | (green << 8) | blue;
                result.setRGB(x, y, invertedColor);
            }
        }

        update(new FiltrationCompletedEvent(result));
    }
}