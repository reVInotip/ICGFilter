package org.example.model.filters.filters;

import dto.FieldType;
import org.example.model.events.FiltrationCompletedEvent;
import org.example.model.filters.Filter;
import org.example.model.filters.FilterPrototype;
import org.example.model.filters.filterModels.ModelPrototype;
import org.example.model.filters.filterModels.customTypes.Matrix;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

@Filter(descr = "Произвольный свёрточный фильтр", icon = "/utils/expert.png")
public class ExpertFilter extends FilterPrototype {
    static private final int[] embossMatrix = {
            -2, -1, 0,
            -1, 1, 1,
            0, 1, 2
    };

    static private final int[] sharpeningMatrix = {
            0, -1, 0,
            -1, 5, -1,
            0, -1, 0
    };

    static private final int[] laplacianSharpeningMatrix = {
            -1, -1, -1,
            -1, 9, -1,
            -1, -1, -1
    };

    static private final int[] boxBlurMatrix = {
            1, 1, 1,
            1, 1, 1,
            1, 1, 1
    };

    static private final int[] cornerDetectorMatrix = {
            -1, -1, 1,
            -1, 0, 1,
            -1, 1, 1
    };

    public ExpertFilter(ModelPrototype filterModel) {
        super(filterModel);

        var embossParamsList = new ArrayList<>();
        embossParamsList.add(embossMatrix);
        embossParamsList.add(3);
        embossParamsList.add(1);

        filterModel.addRuntimeParameter("emboss", FieldType.MATRIX, embossParamsList);

        var sharpeningParamsList = new ArrayList<>();
        sharpeningParamsList.add(sharpeningMatrix);
        sharpeningParamsList.add(3);
        sharpeningParamsList.add(1);

        filterModel.addRuntimeParameter("sharpening", FieldType.MATRIX, sharpeningParamsList);

        var laplacianSharpeningParamsList = new ArrayList<>();
        laplacianSharpeningParamsList.add(laplacianSharpeningMatrix);
        laplacianSharpeningParamsList.add(3);
        laplacianSharpeningParamsList.add(1);

        filterModel.addRuntimeParameter("laplacian sharpening", FieldType.MATRIX, laplacianSharpeningParamsList);

        var boxBlurParamsList = new ArrayList<>();
        boxBlurParamsList.add(boxBlurMatrix);
        boxBlurParamsList.add(3);
        boxBlurParamsList.add(9);

        filterModel.addRuntimeParameter("box blur", FieldType.MATRIX, boxBlurParamsList);

        var cornerDetectorParamsList = new ArrayList<>();
        cornerDetectorParamsList.add(cornerDetectorMatrix);
        cornerDetectorParamsList.add(3);
        cornerDetectorParamsList.add(1);

        filterModel.addRuntimeParameter("corner detector", FieldType.MATRIX, cornerDetectorParamsList);
    }

    @Override
    public void convert(BufferedImage image, BufferedImage result) {
        int divider = filterModel.getInteger("divider");
        Matrix kernel = filterModel.getMatrix("kernel");

        int halfSize = kernel.getWidth() / 2;
        int k = kernel.getWidth() % 2;

        int color, red, green, blue;
        int sumRed = 0, sumGreen = 0, sumBlue = 0;
        for (int i = 0; i < image.getHeight(); ++i) {
            for (int j = 0; j < image.getWidth(); ++j) {

                for (int y = -halfSize; y < halfSize + k; ++y)  {
                    for (int x = -halfSize; x < halfSize + k; ++x) {
                        if (j + x >= image.getWidth() || i + y >= image.getHeight() || j + x < 0 || i + y < 0) {
                            color = 255 | (255 << 8) | (255 << 16);
                        } else {
                            color = image.getRGB(j + x, i + y);
                        }

                        red = (color & 0xff0000) >> 16;
                        green = (color & 0xff00) >> 8;
                        blue = color & 0xff;

                        sumRed += red * kernel.get(x + halfSize, y + halfSize);
                        sumGreen += green * kernel.get(x + halfSize, y + halfSize);
                        sumBlue += blue * kernel.get(x + halfSize, y + halfSize);
                    }
                }

                sumRed /= divider;
                sumBlue /= divider;
                sumGreen /= divider;

                sumRed = Math.min(Math.max(sumRed, 0), 255);
                sumBlue = Math.min(Math.max(sumBlue, 0), 255);
                sumGreen = Math.min(Math.max(sumGreen, 0), 255);

                color = (255 << 24) | (sumRed << 16) | (sumGreen << 8) | (sumBlue);

                result.setRGB(j, i, color);

                sumBlue = 0;
                sumRed = 0;
                sumGreen = 0;
            }
        }

        update(new FiltrationCompletedEvent(result));
    }
}
