package org.example.model.filters.filters;

import org.example.model.events.FiltrationCompletedEvent;
import org.example.model.filters.Filter;
import org.example.model.filters.FilterPrototype;
import org.example.model.filters.filterModels.ModelPrototype;

import java.awt.image.BufferedImage;

@Filter(descr = "изменение гаммы", icon = "/utils/gamma.png")
public class GammaCorrection extends FilterPrototype {
    public GammaCorrection(ModelPrototype filterModel) {
        super(filterModel);
    }

    @Override
    public void convert(BufferedImage image) {
        int color, red, green, blue;

        Double gamma = filterModel.getDouble("gamma"); // replace with double or float

        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < image.getHeight(); ++i) {
            for (int j = 0; j < image.getWidth(); ++j) {
                color = image.getRGB(j, i);
                red = (int) (255 * Math.pow(((color & 0xff0000) >> 16) / 255.0, 1.0 / gamma));
                green = (int) (255 * Math.pow(((color & 0xff00) >> 8) / 255.0, 1.0 / gamma));
                blue = (int) (255 * Math.pow((color & 0xff) / 255.0, 1.0 / gamma));

                color = blue | (green << 8) | (red << 16);
                result.setRGB(j, i, color);
            }
        }

        update(new FiltrationCompletedEvent(result));
    }
}
