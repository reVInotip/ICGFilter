package org.example.view.components.filterDialogs;

import dto.FilterParam;
import org.example.model.filters.filterModels.ModelPrototype;
import org.example.model.filters.filterModels.customTypes.Matrix;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class DialogPrototype extends JDialog {
    private final ModelPrototype model;
    private final GridBagConstraints gbc;
    private final JButton apply;

    private int addSimpleElement(JPanel panel, String paramName, int max, int min, Integer step, int y) {
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

        return y + 1;
    }

    private int addDoubleElement(JPanel panel, String paramName, double max, double min, int y) {
        final JTextField textField = new JTextField(5);
        textField.setFont(new Font("Arial", Font.PLAIN, 14));

        textField.setText(String.valueOf(min)); // Устанавливаем начальное значение. тут наверное будет баг

        final int SCALE = 100;
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

        AtomicReference<ArrayList<JTextField>> fields = new AtomicReference<>(updateMatrixPanel(matrixPanel, matrix.getWidth(), paramName));

        sizeSpinner.addChangeListener(e -> {
            int newSize = (int) sizeSpinner.getValue();
            matrixPanel.setLayout(new GridLayout(0, newSize, 5, 5));
            fields.set(updateMatrixPanel(matrixPanel, newSize, paramName));
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

        return y + 1;
    }

    private ArrayList<JTextField> updateMatrixPanel(JPanel matrixPanel, int size, String paramName) {
        matrixPanel.removeAll();

        ArrayList<JTextField> fields = new ArrayList<>();
        // Создаем текстовые поля для каждого элемента матрицы
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                JTextField field = new JTextField(3);
                field.setHorizontalAlignment(JTextField.CENTER);
                field.setText(String.valueOf(model.getMatrix(paramName).safetyGet(x, y)));
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
                    return addSimpleElement(panel, element.name, element.max, element.min, element.step, y);
                }
                case DOUBLE -> {
                    return addDoubleElement(panel, element.name, element.max, element.min, y);
                }
                case MATRIX -> {
                    return addMatrixElement(panel, element.name, element.max, element.min, y);
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
    }
}
