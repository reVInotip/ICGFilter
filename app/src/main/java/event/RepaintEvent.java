package event;

import java.awt.image.BufferedImage;

public class RepaintEvent implements Event {
    public BufferedImage image;

    public RepaintEvent(BufferedImage image) {
        this.image = image;
    }
}
