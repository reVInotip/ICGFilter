package org.example.model.filters;

import org.example.model.events.ModelObservable;
import org.example.model.filters.filterModels.ModelPrototype;

import java.awt.image.BufferedImage;

public abstract class FilterPrototype extends ModelObservable {
    protected ModelPrototype filterModel;

    public FilterPrototype(ModelPrototype filterModel) {
        this.filterModel = filterModel;
    }

    public void convert(BufferedImage image, BufferedImage result) {

    }
}
