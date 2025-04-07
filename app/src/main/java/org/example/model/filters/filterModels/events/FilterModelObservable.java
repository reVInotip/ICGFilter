package org.example.model.filters.filterModels.events;

import org.example.event.Event;
import org.example.event.observers.Observer;

import java.util.ArrayList;
import java.util.List;

public class FilterModelObservable {
    private final List<FilterModelObserver> observers = new ArrayList<>();

    public void add(FilterModelObserver observer) {
        observers.add(observer);
    }

    public void update(FilterModelEvent event){
        observers.forEach(observer -> observer.update(event));
    }
}
