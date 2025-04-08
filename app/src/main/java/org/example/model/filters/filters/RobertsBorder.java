package org.example.model.filters.filters;

import org.example.model.events.FiltrationCompletedEvent;
import org.example.model.filters.Filter;
import org.example.model.filters.FilterPrototype;
import org.example.model.filters.filterModels.ModelPrototype;

import java.awt.image.BufferedImage;

@Filter(descr = "Выделение границ (Робертс)", icon = "/utils/borders.png")
public class RobertsBorder extends FilterPrototype {
    final private int[] robertsX = {
            1, 0,
            0, -1
    };

    final private int[] robertsY = {
            0, 1,
            -1, 0
    };

    public RobertsBorder(ModelPrototype filterModel) {
        super(filterModel);
    }

    @Override
    public void convert(BufferedImage image, BufferedImage result) {
        int binParam = filterModel.getInteger("binParam");

        int color, red, green, blue;
        int rdx = 0, gdx = 0, bdx = 0;
        int rdy = 0, gdy = 0, bdy = 0;
        int gradRed = 0, gradGreen = 0, gradBlue = 0;
        for (int i = 0; i < image.getHeight(); ++i) {
            for (int j = 0; j < image.getWidth(); ++j) {

                for (int y = 0; y < 2; ++y) {
                    for (int x = 0; x < 2; ++x) {
                        if (j + x >= image.getWidth() || i + y >= image.getHeight()) {
                            color = 255 | (255 << 8) | (255 << 16);
                        } else {
                            color = image.getRGB(j + x, i + y);
                        }

                        red = (color & 0xff0000) >> 16;
                        green = (color & 0xff00) >> 8;
                        blue = color & 0xff;

                        rdx += red * robertsX[y * 2 + x];
                        bdx += blue * robertsX[y * 2 + x];
                        gdx += green * robertsX[y * 2 + x];

                        rdy += red * robertsY[y * 2 + x];
                        bdy += blue * robertsY[y * 2 + x];
                        gdy += green * robertsY[y * 2 + x];
                    }
                }

                gradRed = Math.abs(rdx) + Math.abs(rdy);
                gradGreen = Math.abs(gdx) + Math.abs(gdy);
                gradBlue = Math.abs(bdx) + Math.abs(bdy);

                red = gradRed > binParam ? 255 : 0;
                green = gradGreen > binParam ? 255 : 0;
                blue = gradBlue > binParam ? 255 : 0;

                color = blue | (green << 8) | (red << 16) | (255 << 24);
                result.setRGB(j, i, color);

                rdx = 0;
                bdx = 0;
                gdx = 0;

                rdy = 0;
                bdy = 0;
                gdy = 0;
            }
        }

        update(new FiltrationCompletedEvent(result));
    }
}
