package org.example.model.filters.filters;//

import org.example.model.events.FiltrationCompletedEvent;
import org.example.model.filters.Filter;
import org.example.model.filters.FilterPrototype;
import org.example.model.filters.filterModels.ModelPrototype;

import java.awt.image.BufferedImage;

@Filter(descr = "делает изображение чёрно-белым", icon = "/utils/blackAndWhite.png")
public class ConverterToBlackAndWhite extends FilterPrototype {
    public ConverterToBlackAndWhite(ModelPrototype filterModel) {
        super(filterModel);
    }

    public void convert(BufferedImage image, BufferedImage result) {
       for(int x = 0; x < image.getWidth(); ++x) {
            for(int y = 0; y < image.getHeight(); ++y) {
                result.setRGB(x, y, newColorOfPixel(image.getRGB(x, y)));
            }
       }

       update(new FiltrationCompletedEvent(result));
    }

    private int newColorOfPixel(int color) {
        int a1 = color >> 24 & 255;
        int r1 = color >> 16 & 255;
        int g1 = color >> 8 & 255;
        int b1 = color & 255;
        int average = (r1 + g1 + b1) / 3;
        return 255 << 24 | average << 16 | average << 8 | average;
    }
}
