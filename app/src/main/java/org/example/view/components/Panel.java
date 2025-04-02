package org.example.view.components;

import org.example.event.Event;
import org.example.event.RepaintEvent;
import org.example.event.ResizeImgEvent;
import org.example.event.ShiftImgEvent;
import org.example.event.observers.Observer;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

//public class Panel extends JPanel implements Observer, MouseListener, MouseMotionListener, MouseWheelListener {
public class Panel extends JPanel implements Observer{
    private Dimension panelSize; // Видимый размер изображения
    private BufferedImage img; // изображение
    private Dimension imSize = null;   // Реальный размер изображения

    public Panel() {
        super();
        setBackground(Color.GRAY);
    }
    @Override
    public void update(Event event) {
        if (event instanceof RepaintEvent repaintEvent) {
            if (repaintEvent.image != null) {
                this.img = repaintEvent.image;

                imSize = new Dimension(img.getWidth(), img.getHeight());
                panelSize = new Dimension(imSize);

                setPreferredSize(panelSize);
            }
            CursorManager.defaultCursor();
        } else if (event instanceof ResizeImgEvent resizeImgEvent) {
            if (img == null || imSize == null) {
                return;
            }

            // Обновление размеров панели
            panelSize.width = (int) (panelSize.width * resizeImgEvent.magnificationSize);
            panelSize.height = (int) ((long) panelSize.width * imSize.height / imSize.width);

            setPreferredSize(panelSize);
        } else if (event instanceof ShiftImgEvent shiftImgEvent) {
            // Получаем текущую позицию прокрутки
            if (getParent() instanceof JViewport viewport) {
                Point scroll = viewport.getViewPosition();
                Dimension viewportSize = viewport.getSize();

                int newScrollX = scroll.x + shiftImgEvent.deltaX;
                int newScrollY = scroll.y + shiftImgEvent.deltaY;


                if (newScrollX < 0 || newScrollX > panelSize.width - viewportSize.width) {
                    return;
                }

                if (newScrollY < 0 || newScrollY > panelSize.height - viewportSize.height) {
                    return;
                }

                viewport.setViewPosition(new Point(newScrollX, newScrollY));
            }
        }

        revalidate();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (img != null) {
            // Отрисовка изображения с учетом масштабирования
            g.drawImage(img, 0, 0, panelSize.width, panelSize.height, this);

            Graphics2D g2d = (Graphics2D) g.create();

            float[] dash = {10, 5};
            BasicStroke dashedStroke = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, dash, 0);
            g2d.setStroke(dashedStroke);

            g2d.setColor(Color.RED);

            g2d.drawRect(1, 1, panelSize.width - 1, panelSize.height - 1);

            g2d.dispose();
        }
    }
}