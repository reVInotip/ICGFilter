package org.example.model.filters.filters;

import org.example.model.events.FiltrationCompletedEvent;
import org.example.model.filters.Filter;
import org.example.model.filters.FilterPrototype;
import org.example.model.filters.filterModels.ModelPrototype;

import java.awt.Color;
import java.awt.image.BufferedImage;

@Filter(descr = "Эфект лампочки", icon = "/utils/light.png")
public class LightFilter extends FilterPrototype {
    public LightFilter(ModelPrototype filterModel) {
        super(filterModel);
    }

    @Override
    public void convert(BufferedImage image, BufferedImage result) {
        int centerX = filterModel.getInteger("centerX");
        int centerY = filterModel.getInteger("centerY");
        float radius = filterModel.getInteger("radius");

        // Фиксированные параметры света (как в оригинальной версии)
        Color startColor = new Color(255, 255, 255, 100); // Белый свет с прозрачностью 100
        Color endColor = new Color(255, 255, 255, 0);     // Полностью прозрачный на границе

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                double distance = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2));
                double normalizedDistance = Math.min(distance / radius, 1.0);

                int alpha = (int) (startColor.getAlpha() + (endColor.getAlpha() - startColor.getAlpha()) * normalizedDistance);
                Color gradientColor = new Color(255, 255, 255, alpha);
                Color originalColor = new Color(image.getRGB(x, y), true);
                Color finalColor = blendColors(originalColor, gradientColor);

                result.setRGB(x, y, finalColor.getRGB());
            }
        }

        update(new FiltrationCompletedEvent(result));
    }

    private Color blendColors(Color original, Color gradient) {
        float alpha = gradient.getAlpha() / 255.0f;
        float inverseAlpha = 1.0f - alpha;

        int red = (int) (original.getRed() * inverseAlpha + gradient.getRed() * alpha);
        int green = (int) (original.getGreen() * inverseAlpha + gradient.getGreen() * alpha);
        int blue = (int) (original.getBlue() * inverseAlpha + gradient.getBlue() * alpha);
        int alphaFinal = Math.min(255, original.getAlpha() + gradient.getAlpha());

        return new Color(red, green, blue, alphaFinal);
    }
}