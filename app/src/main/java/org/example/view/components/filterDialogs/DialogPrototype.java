package org.example.view.components.filterDialogs;

import dto.FilterParam;
import org.example.model.filters.filterModels.ModelPrototype;
import org.example.model.filters.filterModels.customTypes.Matrix;
import org.example.model.filters.filterModels.events.FilterModelEvent;
import org.example.model.filters.filterModels.events.FilterModelObserver;
import org.example.model.filters.filterModels.events.UpdateMatrixEvent;
import org.example.utils.Pair;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class DialogPrototype extends JDialog implements FilterModelObserver {
    private final ModelPrototype model;
    private final GridBagConstraints gbc;
    private final JButton apply;
    private final HashMap<String, List<Object>> updatedElements = new HashMap<>();

    private final int SCALE = 100;

    private int addIntegerElement(JPanel panel, String paramName, int max, int min, Integer step, int y) {
        final JSpinner elementSpinner = new JSpinner(new SpinnerNumberModel(min, min, max, step == null ? 1 : step));
        final JSlider elementSlider = new JSlider(JSlider.HORIZONTAL, min, max, min);
        final JLabel elementLabel = new JLabel(paramName);

        elementSlider.addChangeListener(changeEvent -> {
            int value = elementSlider.getValue();
            elementSpinner.setValue(value);
        });

        elementSpinner.addChangeListener(changeEvent -> {
            int value = Integer.parseInt(String.valueOf(elementSpinner.getValue()));
            elementSlider.setValue(value);
        });

        apply.addActionListener(actionEvent -> {
            int value = elementSlider.getValue();
            model.setInteger(paramName, value);
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

        var ue = new ArrayList<>();
        ue.add(elementSpinner);
        ue.add(elementSlider);

        updatedElements.put(paramName, ue);

        return y + 1;
    }

    private int addDoubleElement(JPanel panel, String paramName, double max, double min, int y) {
        final JTextField textField = new JTextField(5);
        textField.setFont(new Font("Arial", Font.PLAIN, 14));

        textField.setText(String.valueOf(min)); // Устанавливаем начальное значение. тут наверное будет баг

        final JSlider slider = new JSlider(JSlider.HORIZONTAL,
                (int)(min * SCALE),
                (int)(max * SCALE),
                (int)(min * SCALE));

        final JLabel label = new JLabel(paramName);

        textField.addActionListener(e -> {
            try {
                double value = Double.parseDouble(textField.getText());
                slider.setValue((int)(value * SCALE));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel,
                        "Введите корректное значение",
                        "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        slider.addChangeListener(e -> {
            double value = slider.getValue() / (double)SCALE;
            textField.setText(String.valueOf(value));
        });

        apply.addActionListener(actionEvent -> {
            double value = slider.getValue() / (double)SCALE;
            model.setDouble(paramName, value);
        });

        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.gridy = y;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(textField, gbc);

        gbc.gridx = 1;
        gbc.gridy = y + 1;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(slider, gbc);

        var ue = new ArrayList<>();
        ue.add(textField);
        ue.add(slider);

        updatedElements.put(paramName, ue);

        return y + 1;
    }

    private int addMatrixDataElement(JPanel panel, String paramName, List<Integer> sizes, int y) {
        final JComboBox<Integer> sizeForRedChannel = new JComboBox<>(sizes.toArray(new Integer[0]));
        final JLabel labelForRedCh = new JLabel(paramName + " (красный канал)");

        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(labelForRedCh, gbc);

        gbc.gridx = 1;
        gbc.gridy = y;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(sizeForRedChannel, gbc);

        final JComboBox<Integer> sizeForBlueChannel = new JComboBox<>(sizes.toArray(new Integer[0]));
        final JLabel labelForBlueCh = new JLabel(paramName + " (синий канал)");

        gbc.gridx = 0;
        gbc.gridy = y + 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(labelForBlueCh, gbc);

        gbc.gridx = 1;
        gbc.gridy = y + 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(sizeForBlueChannel, gbc);

        final JComboBox<Integer> sizeForGreenChannel = new JComboBox<>(sizes.toArray(new Integer[0]));
        final JLabel labelForGreenCh = new JLabel(paramName + " (зелёный канал)");

        gbc.gridx = 0;
        gbc.gridy = y + 2;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(labelForGreenCh, gbc);

        gbc.gridx = 1;
        gbc.gridy = y + 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(sizeForGreenChannel, gbc);

        apply.addActionListener(actionEvent -> {
            int newSizeForGreen = Integer.parseInt(sizeForGreenChannel.getSelectedItem().toString());
            model.setMatrixData(paramName, newSizeForGreen, "green");

            int newSizeForBlue = Integer.parseInt(sizeForBlueChannel.getSelectedItem().toString());
            model.setMatrixData(paramName, newSizeForBlue, "blue");

            int newSizeForRed = Integer.parseInt(sizeForRedChannel.getSelectedItem().toString());
            model.setMatrixData(paramName, newSizeForRed, "red");
        });

        return y + 2;
    }

    private int addMatrixElement(JPanel panel, String paramName, int maxSize, int minSize, int y) {
        Matrix matrix = model.getMatrix(paramName);

        JLabel sizeLabel = new JLabel("Размер матрицы:");
        JSpinner sizeSpinner = new JSpinner(new SpinnerNumberModel(matrix.getWidth(), minSize, maxSize, 1));

        JPanel matrixPanel = new JPanel();

        matrixPanel.setLayout(new GridLayout(0, minSize, 5, 5));

        AtomicReference<ArrayList<JTextField>> fields = new AtomicReference<>(updateMatrixPanel(matrixPanel, matrix, matrix.getWidth()));

        sizeSpinner.addChangeListener(e -> {
            int newSize = (int) sizeSpinner.getValue();
            fields.set(updateMatrixPanel(matrixPanel, matrix, newSize));
            panel.revalidate();
        });



        apply.addActionListener(actionEvent -> {
            int newSize = (int) sizeSpinner.getValue();
            model.getMatrix(paramName).resize(newSize, newSize);

            for (int i = 0; i < newSize; i++) {
                for (int j = 0; j < newSize; j++) {
                    JTextField field = fields.get().get(i * newSize + j);

                    try {
                        int value = Integer.parseInt(field.getText());
                        model.setMatrix(paramName, j, i, value);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(matrixPanel,
                                "Введите целое число", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(sizeLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = y;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(sizeSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = y + 1;
        gbc.gridwidth = 2;
        panel.add(matrixPanel, gbc);
        gbc.gridwidth = 1;

        var ue = new ArrayList<>();
        ue.add(panel);
        ue.add(matrixPanel);
        ue.add(fields);
        ue.add(sizeSpinner);

        updatedElements.put(paramName, ue);

        return y + 1;
    }

    private int addStringList(JPanel panel, String paramName, List<String> elements, int y) {
        JButton button = new JButton(paramName);

        JPopupMenu popupMenu = new JPopupMenu();
        for (String element: elements) {
            JMenuItem item = new JMenuItem(element);
            if (model.getLinkElements(paramName) != null) {
                item.addActionListener(actionEvent -> {
                    List<String> linkEl = model.getLinkElements(paramName);
                    if (model.isMatrix(linkEl.getFirst()) && model.isInteger(linkEl.get(1))) {
                        model.update(new UpdateMatrixEvent(linkEl.getFirst(), linkEl.get(1), element));
                    }
                });
            }
            popupMenu.add(item);
        }

        if (model.getLinkElements(paramName) == null) {
            apply.addActionListener(actionEvent -> {
                int selectedElement = popupMenu.getSelectionModel().getSelectedIndex();
                model.setListElement(paramName, selectedElement);
            });
        }

        button.addActionListener(actionEvent -> {
            popupMenu.show(button, 0, button.getHeight());
        });

        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(button, gbc);

        return y;
    }

    private ArrayList<JTextField> updateMatrixPanel(JPanel matrixPanel, Matrix input, int size) {
        matrixPanel.setLayout(new GridLayout(0, size, 5, 5));
        matrixPanel.removeAll();

        ArrayList<JTextField> fields = new ArrayList<>();
        // Создаем текстовые поля для каждого элемента матрицы
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                JTextField field = new JTextField(3);
                field.setHorizontalAlignment(JTextField.CENTER);
                field.setText(String.valueOf(input.safetyGet(x, y)));
                matrixPanel.add(field);
                fields.add(field);
            }
        }

        return fields;
    }

    private int addElement(JPanel panel, FilterParam element, int y) {
        if (element.isValid()) {
            switch (element.type) {
                case INTEGER -> {
                    return addIntegerElement(panel, element.name, element.max, element.min, element.step, y);
                }
                case DOUBLE -> {
                    return addDoubleElement(panel, element.name, element.max, element.min, y);
                }
                case MATRIX -> {
                    return addMatrixElement(panel, element.name, element.max, element.min, y);
                }
                case LIST -> {
                    return addStringList(panel, element.name, element.elements, y);
                }
                case MATRIX_DATA -> {
                    return addMatrixDataElement(panel, element.name, element.size, y);
                }
            }
        }
        return y;
    }

    public DialogPrototype(JFrame parent, String name, HashMap<String, FilterParam> dialogElements, ModelPrototype model) {
        super(parent, "Окно настроек для инструмента: " + name, true);
        this.model = model;
        apply = new JButton("Apply");

        apply.setBackground(Color.GREEN);

        apply.addActionListener(actionEvent -> {
            DialogPrototype.this.setVisible(false);
        });

        JPanel paramsPanel = new JPanel(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5); // Отступы между компонентами

        int y = 0;
        for (Map.Entry<String, FilterParam> dialogElement: dialogElements.entrySet()) {
            y += addElement(paramsPanel, dialogElement.getValue(), y) + 1;
        }

        paramsPanel.setPreferredSize(new Dimension(300, 300));

        add(new JScrollPane(paramsPanel), BorderLayout.CENTER);
        add(apply, BorderLayout.SOUTH);

        setMinimumSize(new Dimension(300, 300));
        setLocationRelativeTo(parent);

        model.add(this);
    }

    @Override
    public void update(FilterModelEvent event) {
        if (event instanceof UpdateMatrixEvent updatedEvent) {
            List<Object> updatedEl = updatedElements.get(updatedEvent.paramName);
            JPanel mainPanel = (JPanel) updatedEl.getFirst();
            JPanel matrixPanel = (JPanel) updatedEl.get(1);
            AtomicReference<ArrayList<JTextField>> fields = (AtomicReference<ArrayList<JTextField>>) updatedEl.get(2);
            JSpinner sizeSpinner = (JSpinner) updatedEl.get(3);

            Pair<Matrix, Integer> data = (Pair<Matrix,Integer>) model.getRuntimeParameter(updatedEvent.matrixName).parameter;

            Matrix input = data.first;
            sizeSpinner.setValue(input.getWidth());

            fields.set(updateMatrixPanel(matrixPanel, input, input.getWidth()));
            mainPanel.revalidate();

            updatedEl = updatedElements.get(updatedEvent.dividerName);
            JSpinner dividerSpinner = (JSpinner) updatedEl.getFirst();
            JSlider dividerSlider = (JSlider) updatedEl.get(1);
            int divider = data.second;

            dividerSlider.setValue(divider);
           dividerSpinner.setValue(divider);
        }
    }
}
