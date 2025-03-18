package view.components;

import javax.swing.*;
import java.awt.*;

public class Frame extends JFrame {
    private static final String WINDOW_NAME = "ICG Filter";
    private static final Dimension MIN_DIMENSION = new Dimension(640, 480);

    private void configure(int width, int height) {
        Dimension prefferdDimension = new Dimension(width, height);
        setPreferredSize(prefferdDimension);
        setMinimumSize(MIN_DIMENSION);

        setLocation((Toolkit.getDefaultToolkit().getScreenSize().width - width) / 2,
                (Toolkit.getDefaultToolkit().getScreenSize().height - height) / 2);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    protected Frame(int width, int height) {
        super(WINDOW_NAME);
        configure(width, height);
        setVisible(false);
    }

    protected void showFrame() {
        setVisible(true);
        pack();
    }
}
