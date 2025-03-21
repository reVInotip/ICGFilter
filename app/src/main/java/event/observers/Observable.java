package event.observers;

import event.Event;
import java.util.ArrayList;
import java.util.List;

public class Observable {
    private List<Observer> observers = new ArrayList<>();

    public void add(Observer observer) {
        observers.add(observer);
    }

    public void update(Event event){
        observers.forEach(observer -> observer.update(event));
    }
}
