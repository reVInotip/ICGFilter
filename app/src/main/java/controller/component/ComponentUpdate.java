package controller.component;

import model.MainModel;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class ComponentUpdate extends ComponentAdapter {
    private final MainModel model;

    public ComponentUpdate(MainModel model) {
        this.model = model;
    }

    @Override
    public void componentResized(ComponentEvent componentEvent) {
        model.stateChanged();
    }
}
