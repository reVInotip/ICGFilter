package view.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class Frame extends JFrame {
    private static final String WINDOW_NAME = "ICG Filter";
    private static final Dimension MIN_DIMENSION = new Dimension(640, 480);

    private final JMenuBar menuBar = new JMenuBar();
    private final HashMap<String, Integer> menuHashMap = new HashMap<>();
    private int menuIndex;

    private final JToolBar toolBar = new JToolBar();

    protected final Panel panel = new Panel();

    private static class FilterItem {
        public AbstractButton button;
        public JMenuItem item;
    }

    private final HashMap<String, FilterItem> filtersHashMap = new HashMap<>();

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

        setJMenuBar(menuBar);

        toolBar.addSeparator();
        add(toolBar, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);

        ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
        toolTipManager.setInitialDelay(500);  // Задержка перед появлением (в миллисекундах)
    }

    protected void showFrame() {
        setVisible(true);
        pack();
    }

    protected void addMenuItem(String title, String name, ActionListener listener) {
        JMenuItem item = menuBar.getMenu(menuHashMap.get(title)).add(name);
        item.addActionListener(listener);
    }

    protected void addMenu(String title) {
        JMenu menu = new JMenu(title);
        menuHashMap.put(title, menuIndex);
        menuBar.add(menu);

        ++menuIndex;
    }

    protected void addToolbarButton(String title, String descr, String iconPath, ActionListener listener) {
        if (title == null && iconPath == null) {
            System.err.println("Component should have name or icon");
        }

        JButton button = new JButton();
        button.addActionListener(listener);
        if (descr != null) {
            button.setToolTipText(descr);
        }

        if (iconPath != null) {
            try {
                ImageIcon icon = new ImageIcon(
                        Frame.class.getResource(iconPath)
                );
                Image image = icon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
                button.setIcon(new ImageIcon(image));
            } catch (NullPointerException e) {
                System.err.println("Can not load icon for button: " + title + " because " + e.getMessage());
            }
        } else {
            button.setName(title);
        }

        toolBar.add(button);
    }

    protected void addToolbarButtonGroup(JRadioButton[] items) {
        ButtonGroup buttonGroup = new ButtonGroup();
        toolBar.addSeparator();
        for (JRadioButton button: items) {
            button.setPreferredSize(new Dimension(40, 40));
            buttonGroup.add(button);
            toolBar.add(button);
        }
        toolBar.addSeparator();
    }

    protected void addToolbarSeparator() {
        toolBar.addSeparator();
    }

    protected void addFilterGroup(String[] items, Map<String, String> toolsDescr, Map<String, String> icons) {
        ButtonGroup toolbarButtonGroup = new ButtonGroup();
        ButtonGroup menuButtonGroup = new ButtonGroup();

        for (String tool: items) {
            JRadioButton button = new JRadioButton(tool);
            if (toolsDescr.containsKey(tool)) {
                button.setToolTipText(toolsDescr.get(tool));
            }

            if (icons.containsKey(tool)) {
                try {
                    ImageIcon icon = new ImageIcon(
                            Frame.class.getResource(icons.get(tool))
                    );
                    Image image = icon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
                    button.setIcon(new ImageIcon(image));
                } catch (NullPointerException e) {
                    System.err.println("Can not load icon for tool: " + tool + " because " + e.getMessage());
                }
            }

            toolBar.add(button);
            final boolean[] isClicked = {false, false};

            JRadioButtonMenuItem menuButton = new JRadioButtonMenuItem(tool);
            menuButton.addActionListener(actionEvent -> {
                if (!isClicked[0]) {
                    isClicked[0] = true;
                    button.doClick();
                } else {
                    isClicked[0] = false;
                    isClicked[1] = false;
                }
            });
            button.addActionListener(actionEvent -> {
                if (!isClicked[1]) {
                    toolbarButtonGroup.getElements().asIterator().forEachRemaining(b -> b.setBorderPainted(false));
                    button.setBorderPainted(true);
                    isClicked[1] = true;
                    menuButton.doClick();
                } else {
                    isClicked[0] = false;
                    isClicked[1] = false;
                }
            });

            menuButtonGroup.add(menuButton);
            toolbarButtonGroup.add(button);

            FilterItem filterItem = new FilterItem();
            filterItem.button = menuButton;
            filterItem.item = menuBar.getMenu(menuHashMap.get("View")).add(menuButton);
            filtersHashMap.put(tool, filterItem);
        }
    }

    public void addToolActionListener(String title, ActionListener listener) {
        filtersHashMap.get(title).button.addActionListener(listener);
        filtersHashMap.get(title).item.addActionListener(listener);
    }
}
