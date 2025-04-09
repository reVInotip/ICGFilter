package org.example.view.components;

import dto.FilterDto;
import org.example.model.filters.filterModels.ModelPrototype;
import org.example.view.components.filterDialogs.DialogPrototype;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

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
    private final HashMap<String, DialogPrototype> dialogWindows;

    private void configure(int width, int height) {
        Dimension prefferdDimension = new Dimension(width, height);
        setPreferredSize(prefferdDimension);
        setMinimumSize(MIN_DIMENSION);

        setLocation((Toolkit.getDefaultToolkit().getScreenSize().width - width) / 2,
                (Toolkit.getDefaultToolkit().getScreenSize().height - height) / 2);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    protected Frame(int width, int height, HashMap<String, FilterDto> filterDtos, HashMap<String, ModelPrototype> filterModels) {
        super(WINDOW_NAME);
        configure(width, height);

        setVisible(false);

        setJMenuBar(menuBar);

        //привязка управления курсором
        CursorManager.setTarget(this.getRootPane());

        JScrollPane scrollPaneToToolBar = new JScrollPane(toolBar);
        scrollPaneToToolBar.setPreferredSize(
                new Dimension(
                        110, //не очень важно т.к. она изменяется атоматически
                        53
                )
        );
        add(scrollPaneToToolBar, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);

        DialogsFactory.initFactory(filterDtos, filterModels);
        dialogWindows = DialogsFactory.createDialogs(this);

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

    protected void addMenuButtonGroup(String title, HashMap<String, ActionListener> items, String mainItem) {
        ButtonGroup menuButtonGroup = new ButtonGroup();
        for (Map.Entry<String, ActionListener> entry: items.entrySet()) {
            String name = entry.getKey();
            ActionListener listener = entry.getValue();
            JRadioButtonMenuItem item = new JRadioButtonMenuItem(name);
            item.addActionListener(listener);

            item.setSelected(name.equals(mainItem));

            menuButtonGroup.add(item);
            menuBar.getMenu(menuHashMap.get(title)).add(item);
        }
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

    protected void addFilterGroup(List<String> items, Map<String, String> toolsDescr, Map<String, String> icons) {
        ButtonGroup toolbarButtonGroup = new ButtonGroup();
        ButtonGroup menuButtonGroup = new ButtonGroup();

        for (String tool: items) {
            JRadioButton button = new JRadioButton();
            if (toolsDescr.containsKey(tool)) {
                button.setToolTipText(toolsDescr.get(tool));
            }

            if (icons.containsKey(tool)) {
                try {
                    ImageIcon icon = new ImageIcon(
                            Frame.class.getResource(icons.get(tool))
                    );
                    Image image = icon.getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH);
                    button.setIcon(new ImageIcon(image));
                } catch (NullPointerException e) {
                    System.err.println("Can not load icon for tool: " + tool + " because " + e.getMessage());
                }
            } else {
                button.setText(tool);
            }

            toolBar.add(button);
            final boolean[] isClicked = {false, false};

            JRadioButtonMenuItem menuButton = new JRadioButtonMenuItem(tool);
            menuButton.addActionListener(actionEvent -> {
                if (!isClicked[0]) {
                    isClicked[0] = true;

                    if (isClicked[1]) {
                        isClicked[0] = false;
                        isClicked[1] = false;
                    } else {
                        button.doClick();
                    }
                }
            });

            button.addActionListener(actionEvent -> {
                if (!isClicked[1]) {
                    toolbarButtonGroup.getElements().asIterator().forEachRemaining(b -> b.setBorderPainted(false));
                    button.setBorderPainted(true);
                    isClicked[1] = true;

                    if (isClicked[0]) {
                        isClicked[0] = false;
                        isClicked[1] = false;
                    } else {
                        menuButton.doClick();
                    }
                }
            });

            if (dialogWindows.containsKey(tool)) {
                ItemListener setVisibleListener = itemEvent -> {
                        if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
                            dialogWindows.get(tool).setVisible(true);
                            //menuButtonGroup.clearSelection();
                            toolbarButtonGroup.clearSelection();
                        }
                };

                button.addItemListener(setVisibleListener);
            }

            menuButtonGroup.add(menuButton);
            toolbarButtonGroup.add(button);

            FilterItem filterItem = new FilterItem();
            filterItem.button = menuButton;

            filterItem.item = menuBar.getMenu(menuHashMap.get("Filter")).add(menuButton);
            filtersHashMap.put(tool, filterItem);
        }
    }

    public void addToolActionListener(String title, ActionListener listener) {
        filtersHashMap.get(title).button.addActionListener(listener);
        filtersHashMap.get(title).item.addActionListener(listener);
    }
}
