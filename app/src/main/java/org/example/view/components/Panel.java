package org.example.view.components;

import org.example.event.*;
import org.example.event.Event;
import org.example.event.observers.Observer;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Panel extends JPanel implements Observer {
    private Dimension panelSize; // изменённый размер изображения
    private Dimension previousPanelSize = null;
    private Dimension previousImgSize;// для того чтобы при обновлении картинки тогоже размера ничего не съезжало
    private Dimension imSize = null;//реальный размер изображения
    private BufferedImage img;
    private BufferedImage scaledImage;
    private boolean isFullScreen = false;

    public Object interpolationType = RenderingHints.VALUE_INTERPOLATION_BILINEAR;

    private boolean reDraw = false;

    public Panel() {
        super();
        setBackground(Color.GRAY);
    }

    @Override
    public void update(Event event) {
        switch (event) {
            case RepaintEvent repaintEvent -> handleRepaintEvent(repaintEvent);
            case ResizeImgEvent resizeImgEvent -> handleResizeEvent(resizeImgEvent);
            case ShiftImgEvent shiftImgEvent -> handleShiftEvent(shiftImgEvent);
            case FullScreenEvent fullScreenEvent -> handleFullScreenEvent(fullScreenEvent);
            default -> {}
        }
    }

    private void handleRepaintEvent(RepaintEvent repaintEvent) {
        if (repaintEvent.image != null) {
            this.img = repaintEvent.image;
            imSize = new Dimension(img.getWidth(), img.getHeight());
            if(previousImgSize == null || (previousImgSize.width !=  imSize.width
                    && previousImgSize.height !=  imSize.height)) {
                panelSize = new Dimension(imSize);
                previousImgSize = new Dimension(imSize);
            }

            //требуется перерисовка
            reDraw = true;

            setPreferredSize(panelSize);
        }
        if(isFullScreen){
            toFullScreen();
        }
        CursorManager.defaultCursor();

        revalidate();
        repaint();
    }

    private void handleResizeEvent(ResizeImgEvent resizeImgEvent) {
        if (img == null || imSize == null) {
            return;
        }

        if(isFullScreen){
            toFullScreen();
        }else {
            panelSize.width = (int) (panelSize.width * resizeImgEvent.magnificationSize);
            panelSize.height = (int) ((long) panelSize.width * imSize.height / imSize.width);
        }

        setPreferredSize(panelSize);

        revalidate();
        repaint();
    }

    private JViewport viewport = null;

    private final Point reusablePoint = new Point();
    private void handleShiftEvent(ShiftImgEvent shiftImgEvent) {

        if (viewport == null) {
            if (getParent() instanceof JViewport) {
                viewport = (JViewport) getParent();
            } else {
                return;
            }
        }

        Point scroll = viewport.getViewPosition();
        Dimension viewportSize = viewport.getSize();

        int newScrollX = scroll.x + shiftImgEvent.deltaX;
        int newScrollY = scroll.y + shiftImgEvent.deltaY;

        if (newScrollX >= 0 && newScrollX <= panelSize.width - viewportSize.width &&
                newScrollY >= 0 && newScrollY <= panelSize.height - viewportSize.height) {
            reusablePoint.setLocation(newScrollX, newScrollY);
            viewport.setViewPosition(reusablePoint);
        }
    }

    private void handleFullScreenEvent(FullScreenEvent fullScreenEvent) {
        isFullScreen = !isFullScreen;

        toFullScreen();

        revalidate();
        repaint();
    }

    private void toFullScreen(){
        Container parent = getParent();

        if (parent instanceof JViewport) {
            Container scrollPane = parent.getParent();
            if (scrollPane instanceof JScrollPane) {
                Dimension viewportSize = scrollPane.getSize();

                double widthRatio = (double)viewportSize.width / imSize.width;
                double heightRatio = (double)viewportSize.height / imSize.height;

                double scale = Math.min(widthRatio, heightRatio);

                panelSize.width = (int)(imSize.width * scale - 4);
                panelSize.height = (int)(imSize.height * scale -4);

            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (img == null) {
            return;
        }

        if (previousPanelSize == null){
            previousPanelSize = new Dimension(imSize);
        }

        //можно значительно успростить и использовать для режима режима (не fullScreen)
        // припомощи g.drawImage(img, 0, 0, getWidth(), getHeight(), this);, не используя инерполяцию, но приэтом картинка
        // при ордер дизеринге осветляется(необяснимо но факт)
        if(!isFullScreen) {
            //если не фул скрин у нас два выхода
            if (panelSize.width == imSize.width && panelSize.height == imSize.height) {
                g.drawImage(img, 0, 0, this);
            } else {
                //прийдётся ли перерисовывать картинку или можно оставить прошлый вариант
                if (!reDraw && (panelSize.width == previousPanelSize.width ||
                        panelSize.height == previousPanelSize.height)) {
                    g.drawImage(scaledImage, 0, 0, this);
                } else {
                    //применение инерполяции в случае измененя размера панели или reDraw
                    drawInterpolatedImage((Graphics2D) g);
                    previousPanelSize = new Dimension(panelSize);
                    reDraw = false;
                }
            }
        }
        else {
            //применение инерполяции в случае измененя размера окна в fullScreen
            drawInterpolatedImage((Graphics2D) g);
            previousPanelSize = new Dimension(panelSize);
            //можно использовать другую интерполяцию для fullScreen
        }

        Graphics2D g2d = (Graphics2D) g.create();
        float[] dash = {10, 5};
        BasicStroke dashedStroke = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, dash, 0);
        g2d.setStroke(dashedStroke);
        g2d.setColor(Color.RED);
        g2d.drawRect(1, 1, panelSize.width - 1, panelSize.height - 1);
        g2d.dispose();
    }

    private void drawInterpolatedImage(Graphics2D g2d) {
        scaledImage = new BufferedImage(panelSize.width, panelSize.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = scaledImage.createGraphics();

        g.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                interpolationType
        );

        // Масштабирование
        g.drawImage(
                img,
                AffineTransform.getScaleInstance(
                        (double) panelSize.width / img.getWidth(),
                        (double) panelSize.height / img.getHeight()
                ),
                null
        );
        g.dispose();

        g2d.drawImage(scaledImage, 0, 0, this);

        //g2d.dispose();
    }

    private void drawInterpolatedImage(Graphics g) {
        scaledImage = new BufferedImage(panelSize.width, panelSize.height, BufferedImage.TYPE_INT_ARGB);

        double scaleX = (double)imSize.width / panelSize.width;
        double scaleY = (double)imSize.height / panelSize.height;

        for (int y = 0; y < panelSize.height; y++) {
            for (int x = 0; x < panelSize.width; x++) {
                double srcX = x * scaleX;
                double srcY = y * scaleY;
                int pixel = getInterpolatedPixel(img, srcX, srcY);
                scaledImage.setRGB(x, y, pixel);
            }
        }
        g.drawImage(scaledImage, 0, 0, this);
    }

    //метод инерполяции надо вынести, но пока лень пусть будет тут
    private int getInterpolatedPixel(BufferedImage image, double x, double y) {
        int x1 = (int)Math.floor(x);
        int y1 = (int)Math.floor(y);
        int x2 = x1 + 1;
        int y2 = y1 + 1;

        if (x1 < 0) x1 = 0;
        if (y1 < 0) y1 = 0;
        if (x2 >= image.getWidth()) x2 = image.getWidth() - 1;
        if (y2 >= image.getHeight()) y2 = image.getHeight() - 1;

        double alphaX = x - (double)x1;
        double alphaY = y - (double)y1;

        int c11 = image.getRGB(x1, y1);
        int c21 = image.getRGB(x2, y1);
        int c12 = image.getRGB(x1, y2);
        int c22 = image.getRGB(x2, y2);

        int c1 = interpolateColor(c11, c21, alphaX);
        int c2 = interpolateColor(c12, c22, alphaX);
        return interpolateColor(c1, c2, alphaY);
    }

    private int interpolateColor(int c1, int c2, double alpha) {
        int a1 = c1 >> 24 & 255;
        int r1 = c1 >> 16 & 255;
        int g1 = c1 >> 8 & 255;
        int b1 = c1 & 255;
        int a2 = c2 >> 24 & 255;
        int r2 = c2 >> 16 & 255;
        int g2 = c2 >> 8 & 255;
        int b2 = c2 & 255;

        int a = (int)(a1 * (1.0 - alpha) + a2 * alpha);
        int r = (int)(r1 * (1.0 - alpha) + r2 * alpha);
        int g = (int)(g1 * (1.0 - alpha) + g2 * alpha);
        int b = (int)(b1 * (1.0 - alpha) + b2 * alpha);

        return a << 24 | r << 16 | g << 8 | b;
    }
}