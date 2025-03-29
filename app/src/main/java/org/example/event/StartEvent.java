package org.example.event;

import org.example.controller.component.PanelMouseAdapter;

import java.awt.event.ComponentAdapter;

public class StartEvent implements Event {
    public ComponentAdapter stateChangeAdapter;

    public PanelMouseAdapter panelMouseAdapter;

    public StartEvent(ComponentAdapter stateChangeAdapter, PanelMouseAdapter panelMouseAdapter) {
        this.stateChangeAdapter = stateChangeAdapter;
        this.panelMouseAdapter = panelMouseAdapter;
    }
}
