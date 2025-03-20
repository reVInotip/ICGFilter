package model.filters.filters;

import model.events.FiltrationCompletedEvent;
import model.filters.Filter;
import model.filters.FiltersModel;

import java.awt.image.BufferedImage;

@Filter(descr = "изменение гаммы", icon = "/utils/gamma.png")
public class GammaCorrection extends FiltersModel {
    static private double gamma = 3;

    @Override
    public void convert(BufferedImage image) {
        int color, red, green, blue;

        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < image.getHeight(); ++i) {
            for (int j = 0; j < image.getWidth(); ++j) {
                color = image.getRGB(j, i);
                red = (int) (255 * Math.pow(((color & 0xff0000) >> 16) / 255.0, 1 / gamma));
                green = (int) (255 * Math.pow(((color & 0xff00) >> 8) / 255.0, 1 / gamma));
                blue = (int) (255 * Math.pow((color & 0xff) / 255.0, 1 / gamma));

                color = blue | (green << 8) | (red << 16);
                result.setRGB(j, i, color);
            }
        }

        update(new FiltrationCompletedEvent(result));
    }
}
