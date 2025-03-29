package org.example.controller.component;

import org.example.model.MainModel;

import java.awt.event.*;

public class PanelMouseAdapter implements MouseListener, MouseMotionListener, MouseWheelListener {

    private final MainModel model;

    private int lastX = 0; // Последняя координата X мыши
    private int lastY = 0; // Последняя координата Y мыши

    public PanelMouseAdapter(MainModel model){
        this.model = model;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        //Сохраняем координаты мыши при нажатии
        if ((e.getModifiersEx() & MouseEvent.BUTTON3_DOWN_MASK) == MouseEvent.BUTTON3_DOWN_MASK) {
            lastX = e.getX();
            lastY = e.getY();
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        double zoomK = 0.05;
        double k = 1 - e.getWheelRotation() * zoomK;
        model.resizePanel(k);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (e.getModifiersEx() != MouseEvent.BUTTON3_DOWN_MASK) {
            return;
        }

        int deltaX = lastX - e.getX();
        int deltaY = lastY - e.getY();
        model.shiftPanel(deltaX, deltaY);
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
