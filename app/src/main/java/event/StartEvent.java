package event;

import java.awt.event.ComponentAdapter;

public class StartEvent implements Event {
    public ComponentAdapter stateChangeAdapter;

    public StartEvent(ComponentAdapter stateChangeAdapter) {
        this.stateChangeAdapter = stateChangeAdapter;
    }
}
