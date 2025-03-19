package view.components;

import event.Event;
import event.RepaintEvent;
import event.observers.Observer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;

public class Panel extends JPanel implements Observer {
    private RepaintEvent event;

    public Panel() {
        super();
        setBackground(Color.GRAY);
    }

    @Override
    public void update(Event event) {
        if (event instanceof RepaintEvent repaintEvent) {
            if (repaintEvent.image != null) {
                this.event = repaintEvent;
                setPreferredSize(new Dimension(repaintEvent.image.getWidth(), repaintEvent.image.getHeight()));

                ComponentEvent resizedEvent = new ComponentEvent(this, ComponentEvent.COMPONENT_RESIZED);
                processComponentEvent(resizedEvent);

                paintComponent(getGraphics());
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (event != null && event.image != null) {
            g.drawImage(event.image, 0, 0, this);
        }
    }
}
