package org.example.model.events;

import java.util.ArrayList;
import java.util.List;

public abstract class ModelObservable {
    private final List<ModelObserver> modelObservers = new ArrayList<>();

    public void add(ModelObserver modelObserver) {
        modelObservers.add(modelObserver);
    }

    public void update(ModelEvent event){
        modelObservers.forEach(modelObserver -> modelObserver.update(event));
    }
}
