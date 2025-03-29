package org.example.event;

public class ShiftImgEvent implements Event {
    public int deltaX;
    public int deltaY;

    public ShiftImgEvent(int deltaX, int deltaY){
        this.deltaX = deltaX;
        this.deltaY = deltaY;
    }
}
