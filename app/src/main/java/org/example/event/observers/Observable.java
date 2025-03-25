package org.example.event.observers;

import org.example.event.Event;
import java.util.ArrayList;
import java.util.List;

public class Observable {
    private final List<Observer> observers = new ArrayList<>();

    public void add(Observer observer) {
        observers.add(observer);
    }

    public void update(Event event){
        observers.forEach(observer -> observer.update(event));
    }
}
