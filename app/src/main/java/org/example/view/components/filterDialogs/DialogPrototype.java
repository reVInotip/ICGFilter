package org.example.view.components.filterDialogs;

import dto.FieldType;
import dto.FilterParam;
import org.example.model.filters.filterModels.ModelPrototype;
import jsonParser.parsedConfigurationObjects.DialogElement;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DialogPrototype extends JDialog {
    private String name;
    private ModelPrototype model;
    private GridBagConstraints gbc;

    private void addSimpleElement(JPanel panel, String paramName, int max, int min, Integer step, int y) {
        final JSpinner elementSpinner = new JSpinner(new SpinnerNumberModel(min, min, max, step == null ? 1 : step));
        final JSlider elementSlider = new JSlider(JSlider.HORIZONTAL, min, max, min);
        final JLabel elementLabel = new JLabel(paramName);

        elementSlider.addChangeListener(changeEvent -> {
            int value = elementSlider.getValue();
            model.setInteger(paramName, value);
            elementSpinner.setValue(value);
        });

        elementSpinner.addChangeListener(changeEvent -> {
            int value = Integer.parseInt(String.valueOf(elementSpinner.getValue()));
            model.setInteger(paramName, value);
            elementSlider.setValue(value);
        });

        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(elementLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = y;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(elementSpinner, gbc);

        gbc.gridx = 1;
        gbc.gridy = y + 1;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(elementSlider, gbc);
    }

    private void addElement(JPanel panel, FilterParam element, int y) {
        if (element.type == FieldType.INTEGER) {
            addSimpleElement(panel, element.name, element.max, element.min, element.step, y);
        } else {

        }
    }

    public DialogPrototype(JFrame parent, String name, HashMap<String, FilterParam> dialogElements, ModelPrototype model) {
        super(parent, "Окно настроек для инструмента: " + name, true);
        this.name = name;
        this.model = model;

        JPanel paramsPanel = new JPanel(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5); // Отступы между компонентами

        int y = 0;
        for (Map.Entry<String, FilterParam> dialogElement: dialogElements.entrySet()) {
            addElement(paramsPanel, dialogElement.getValue(), y);
            y += 2;
        }

        paramsPanel.setPreferredSize(new Dimension(300, 300));

        add(new JScrollPane(paramsPanel));
        setMinimumSize(new Dimension(300, 300));
        setLocation((Toolkit.getDefaultToolkit().getScreenSize().width) / 2,
                (Toolkit.getDefaultToolkit().getScreenSize().height) / 2);

    }
}
