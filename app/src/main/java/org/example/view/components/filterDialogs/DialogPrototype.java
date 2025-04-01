package org.example.view.components.filterDialogs;

import dto.FieldType;
import dto.FilterParam;
import org.example.model.filters.filterModels.ModelPrototype;
import org.example.model.filters.filterModels.customTypes.Matrix;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class DialogPrototype extends JDialog {
    private final ModelPrototype model;
    private final GridBagConstraints gbc;

    private int addSimpleElement(JPanel panel, String paramName, int max, int min, Integer step, int y) {
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
                model.setDouble(paramName, value);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel,
                        "Введите корректное значение",
                        "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        slider.addChangeListener(e -> {
            double value = slider.getValue() / (double)SCALE;
            textField.setText(String.valueOf(value));
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

        sizeForRedChannel.addActionListener(actionListener -> {
            int newSize = Integer.parseInt(sizeForRedChannel.getSelectedItem().toString());
            model.setMatrixData(paramName, newSize, "red");
        });

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

        sizeForBlueChannel.addActionListener(actionListener -> {
            int newSize = Integer.parseInt(sizeForBlueChannel.getSelectedItem().toString());
            model.setMatrixData(paramName, newSize, "blue");
        });

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

        sizeForGreenChannel.addActionListener(actionListener -> {
            int newSize = Integer.parseInt(sizeForGreenChannel.getSelectedItem().toString());
            model.setMatrixData(paramName, newSize, "green");
        });

        gbc.gridx = 0;
        gbc.gridy = y + 2;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(labelForGreenCh, gbc);

        gbc.gridx = 1;
        gbc.gridy = y + 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(sizeForGreenChannel, gbc);

        return y + 2;
    }

    private int addMatrixElement(JPanel panel, String paramName, int maxSize, int minSize, int y) {
        JLabel sizeLabel = new JLabel("Размер матрицы:");
        JSpinner sizeSpinner = new JSpinner(new SpinnerNumberModel(minSize, minSize, maxSize, 1));

        JPanel matrixPanel = new JPanel();

        Matrix matrix = model.getMatrix(paramName);

        matrixPanel.setLayout(new GridLayout(0, minSize, 5, 5)); // 3 колонки

        updateMatrixPanel(matrixPanel, matrix, matrix.getWidth(), paramName);

        updateMatrixPanel(matrixPanel, matrix, minSize, paramName);

        sizeSpinner.addChangeListener(e -> {
            int newSize = (int) sizeSpinner.getValue();
            matrixPanel.setLayout(new GridLayout(0, newSize, 5, 5)); // 3 колонки
            Matrix newMatrix = new Matrix(minSize, minSize);
            updateMatrixPanel(matrixPanel, newMatrix, newSize, paramName);
            panel.revalidate();
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

    private void updateMatrixPanel(JPanel matrixPanel, Matrix matrix, int size, String paramName) {
        matrixPanel.removeAll();

        // Создаем текстовые поля для каждого элемента матрицы
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                JTextField field = new JTextField(3);
                field.setHorizontalAlignment(JTextField.CENTER);
                field.setText(String.valueOf(matrix.get(x, y)));

                final int finalX = x;
                final int finalY = y;
                field.addActionListener(e -> {
                    try {
                        int value = Integer.parseInt(field.getText());
                        matrix.set(finalX, finalY, value);
                        model.setMatrix(paramName, matrix);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(matrixPanel,
                                "Введите целое число", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    }
                });

                matrixPanel.add(field);
            }
        }
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

        JPanel paramsPanel = new JPanel(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5); // Отступы между компонентами

        int y = 0;
        for (Map.Entry<String, FilterParam> dialogElement: dialogElements.entrySet()) {
            y += addElement(paramsPanel, dialogElement.getValue(), y) + 1;
        }

        paramsPanel.setPreferredSize(new Dimension(300, 300));

        add(new JScrollPane(paramsPanel));
        setMinimumSize(new Dimension(300, 300));
        setLocationRelativeTo(parent);
    }
}
