package model.events;

import java.awt.image.BufferedImage;

public class FiltrationCompletedEvent implements ModelEvent{
    public BufferedImage image;

    public FiltrationCompletedEvent(BufferedImage image) {
        this.image = image;
    }
}
