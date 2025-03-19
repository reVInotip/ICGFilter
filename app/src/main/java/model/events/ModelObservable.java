package model.events;

import event.Event;

import java.util.ArrayList;
import java.util.List;

public abstract class ModelObservable {
    private List<ModelObserver> modelObservers = new ArrayList<>();

    public void add(ModelObserver modelObserver) {
        modelObservers.add(modelObserver);
    }

    public void update(ModelEvent event){
        modelObservers.forEach(modelObserver -> modelObserver.update(event));
    }
}
