package model.events;

import event.Event;

public interface ModelObserver {
    void update(ModelEvent event);
}
