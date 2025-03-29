package org.example.event;

import java.awt.*;

public class ResizeImgEvent implements Event{
    public double magnificationSize;

    public ResizeImgEvent(double magnificationSize) {
        this.magnificationSize =  magnificationSize;
    }
}
