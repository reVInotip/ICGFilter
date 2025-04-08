package org.example.model.filters.filters;

import org.example.model.events.FiltrationCompletedEvent;
import org.example.model.filters.Filter;
import org.example.model.filters.FilterPrototype;
import org.example.model.filters.filterModels.ModelPrototype;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Filter(descr = "Удаление артефактов", icon = "")
public class RemoveArtefactsFilter extends FilterPrototype {
    private final HashMap<Integer, Integer> colorsHashMap = new HashMap<>();

    public RemoveArtefactsFilter(ModelPrototype filterModel) {
        super(filterModel);
    }

    @Override
    public void convert(BufferedImage image, BufferedImage result) {
        int width = image.getWidth();
        int height = image.getHeight();
        int boundSize = filterModel.getInteger("bound size");

        //ArrayList<Integer> neighbors = new ArrayList<>(boundSize);
        //ArrayList<Integer> neighborsGreen = new ArrayList<>(boundSize);
        //ArrayList<Integer> neighborsBlue = new ArrayList<>(boundSize);

        int borderLeft = -boundSize / 2;
        int borderRight = boundSize / 2 + boundSize % 2;
        int countNeighbours = boundSize * boundSize - 1;

        BufferedImage middle = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                middle.setRGB(x, y, findMostUsedInBoundsColor(image, countNeighbours / 2, borderLeft, borderRight, x, y));
            }
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                result.setRGB(x, y, findMostUsedInBoundsColor(middle, countNeighbours / 8, borderLeft, borderRight, x, y));
            }
        }

        update(new FiltrationCompletedEvent(middle));
    }

    private int findMostUsedInBoundsColor(BufferedImage image, int count, int borderLeft, int borderRight, int x, int y) {
        colorsHashMap.clear();

        int color;
        for (int i = borderLeft; i < borderRight; ++i) {
            for (int j = borderLeft; j < borderRight; ++j) {
                if (i + y > 0 && i + y < image.getHeight() && j + x > 0 && j + x < image.getWidth() && i != 0 && j != 0) {
                    color = image.getRGB(j + x, i + y);
                    if (colorsHashMap.containsKey(color)) {
                        colorsHashMap.replace(color, colorsHashMap.get(color) + 1);
                    } else {
                        colorsHashMap.put(color, 0);
                    }
                }
            }
        }

        color = image.getRGB(x, y);
        for (Map.Entry<Integer, Integer> entry: colorsHashMap.entrySet()) {
            if (entry.getValue() >= count) {
                color = entry.getKey();
                break;
            }
        }

        return color;
    }

    private int findSmallUsedInBoundsColor(BufferedImage image, int count, int borderLeft, int borderRight, int x, int y) {
        colorsHashMap.clear();

        int color;
        for (int i = borderLeft; i < borderRight; ++i) {
            for (int j = borderLeft; j < borderRight; ++j) {
                if (i + y > 0 && i + y < image.getHeight() && j + x > 0 && j + x < image.getWidth() && i != 0 && j != 0) {
                    color = image.getRGB(j + x, i + y);
                    if (colorsHashMap.containsKey(color)) {
                        colorsHashMap.replace(color, colorsHashMap.get(color) + 1);
                    } else {
                        colorsHashMap.put(color, 0);
                    }
                }
            }
        }

        color = image.getRGB(x, y);
        for (Map.Entry<Integer, Integer> entry: colorsHashMap.entrySet()) {
            if (entry.getValue() <= count) {
                color = entry.getKey();
                break;
            }
        }

        return color;
    }
}
