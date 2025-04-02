package org.example.model.filters.filters;

import org.example.model.events.FiltrationCompletedEvent;
import org.example.model.filters.Filter;
import org.example.model.filters.FilterPrototype;
import org.example.model.filters.filterModels.ModelPrototype;

import java.awt.image.BufferedImage;

@Filter(descr = "поворот", icon = "/utils/rotation.png")
public class AffineTransform extends FilterPrototype {

    public AffineTransform(ModelPrototype filterModel) {
        super(filterModel);
    }

    public BufferedImage createResultImage(BufferedImage src) {

        double angle = Math.toRadians(filterModel.getInteger("angle"));
        double sin = Math.abs(Math.sin(angle));
        double cos = Math.abs(Math.cos(angle));

        int newWidth = (int) Math.round(src.getWidth() * cos + src.getHeight() * sin);
        int newHeight = (int) Math.round(src.getWidth() * sin + src.getHeight() * cos);

        return new BufferedImage(newWidth, newHeight, src.getType());
    }

    public void convert(BufferedImage image, BufferedImage result) {

        result = createResultImage(result);

        // Заполняем белым цветом
        for (int y = 0; y < result.getHeight(); y++) {
            for (int x = 0; x < result.getWidth(); x++) {
                result.setRGB(x, y, 0xFFFFFFFF);
            }
        }

        double angle = Math.toRadians(filterModel.getInteger("angle"));
        double[][] matrix = new double[][]{
                {Math.cos(angle), -Math.sin(angle)},
                {Math.sin(angle), Math.cos(angle)}
        };

        int srcWidth = image.getWidth();
        int srcHeight = image.getHeight();
        int resultWidth = result.getWidth();
        int resultHeight = result.getHeight();

        double srcCenterX = (double)srcWidth / 2.0;
        double srcCenterY = (double)srcHeight / 2.0;
        double resultCenterX = (double)resultWidth / 2.0;
        double resultCenterY = (double)resultHeight / 2.0;

        for (int y = 0; y < resultHeight; y++) {
            for (int x = 0; x < resultWidth; x++) {
                double xRel = x - resultCenterX;
                double yRel = y - resultCenterY;

                double srcX = xRel * Math.cos(angle) + yRel * Math.sin(angle) + srcCenterX;
                double srcY = -xRel * Math.sin(angle) + yRel * Math.cos(angle) + srcCenterY;

                if (srcX >= 0 && srcX < srcWidth - 1 && srcY >= 0 && srcY < srcHeight - 1) {
                    int interpolatedColor = getInterpolatedPixel(image, srcX, srcY);
                    result.setRGB(x, y, interpolatedColor);
                }
            }
        }
        update(new FiltrationCompletedEvent(result));
    }

    private int getInterpolatedPixel(BufferedImage image, double x, double y) {
        int x1 = (int)Math.floor(x);
        int y1 = (int)Math.floor(y);
        int x2 = x1 + 1;
        int y2 = y1 + 1;
        double alphaX = x - (double)x1;
        double alphaY = y - (double)y1;
        int c11 = image.getRGB(x1, y1);
        int c21 = image.getRGB(x2, y1);
        int c12 = image.getRGB(x1, y2);
        int c22 = image.getRGB(x2, y2);
        int c1 = interpolateColor(c11, c21, alphaX);
        int c2 = interpolateColor(c12, c22, alphaX);
        return interpolateColor(c1, c2, alphaY);
    }

    private int interpolateColor(int c1, int c2, double alpha) {
        int a1 = c1 >> 24 & 255;
        int r1 = c1 >> 16 & 255;
        int g1 = c1 >> 8 & 255;
        int b1 = c1 & 255;
        int a2 = c2 >> 24 & 255;
        int r2 = c2 >> 16 & 255;
        int g2 = c2 >> 8 & 255;
        int b2 = c2 & 255;
        int a = (int)((double)a1 * (1.0 - alpha) + (double)a2 * alpha);
        int r = (int)((double)r1 * (1.0 - alpha) + (double)r2 * alpha);
        int g = (int)((double)g1 * (1.0 - alpha) + (double)g2 * alpha);
        int b = (int)((double)b1 * (1.0 - alpha) + (double)b2 * alpha);
        return a << 24 | r << 16 | g << 8 | b;
    }
}