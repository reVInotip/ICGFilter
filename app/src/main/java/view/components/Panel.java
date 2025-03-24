package view.components;

import event.Event;
import event.RepaintEvent;
import event.observers.Observer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class Panel extends JPanel implements Observer, MouseListener, MouseMotionListener, MouseWheelListener {
    private RepaintEvent event;
    private Dimension panelSize; // Видимый размер изображения
    private BufferedImage img; // изображение
    private Dimension imSize = null;   // Реальный размер изображения
    private int lastX = 0; // Последняя координата X мыши
    private int lastY = 0; // Последняя координата Y мыши

    public Panel() {
        super();
        setBackground(Color.GRAY);

        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
    }
    //
    //
    //чуть позже, если понадобиться вынесу это в контроллер, но пока как демо версия пусть будет тут!
    // это реализация прибележения и отдаления изображения
    //
    @Override
    public void update(Event event) {
        if (event instanceof RepaintEvent repaintEvent) {
            if (repaintEvent.image != null) {
                this.event = repaintEvent;
                this.img = repaintEvent.image;

                imSize = new Dimension(img.getWidth(), img.getHeight());
                if (panelSize == null) {
                    panelSize = new Dimension(imSize);
                }

                setPreferredSize(panelSize);

                revalidate();
                repaint();
            }
        }
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

    @Override
    public void mousePressed(MouseEvent e) {
        // Сохраняем координаты мыши при нажатии
        if ((e.getModifiersEx() & MouseEvent.BUTTON3_DOWN_MASK) == MouseEvent.BUTTON3_DOWN_MASK) {
            lastX = e.getX();
            lastY = e.getY();
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (e.getModifiersEx() != MouseEvent.BUTTON3_DOWN_MASK) {
            return; // Если не правая кнопка, выходим из метода
        }

        // Получаем текущую позицию прокрутки
        if (getParent() instanceof JViewport viewport) {
            Point scroll = viewport.getViewPosition();
            Dimension viewportSize = viewport.getSize();

            int deltaX = lastX - e.getX();
            int deltaY = lastY - e.getY();

            int newScrollX = scroll.x + deltaX;
            int newScrollY = scroll.y + deltaY;


            if (newScrollX < 0 || newScrollX > panelSize.width - viewportSize.width) {
                return;
            }

            if (newScrollY < 0 || newScrollY > panelSize.height - viewportSize.height) {
                return;
            }

            viewport.setViewPosition(new Point(newScrollX, newScrollY));

            repaint();
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (img == null || imSize == null) {
            return;
        }

        double zoomK = 0.05;
        double k = 1 - e.getWheelRotation() * zoomK;

        int newPW = (int) (panelSize.width * k);
        if (newPW == (int) (newPW * (1 + zoomK))) {
            return;
        }

        // Обновление размеров панели
        panelSize.width = newPW;
        panelSize.height = (int) ((long) panelSize.width * imSize.height / imSize.width);

        setPreferredSize(panelSize);

        revalidate();
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {}
}