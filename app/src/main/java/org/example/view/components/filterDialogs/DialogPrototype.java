package org.example.view.components.filterDialogs;

import dto.FieldType;
import dto.FilterParam;
import org.example.model.filters.filterModels.ModelPrototype;
import org.example.model.filters.filterModels.customTypes.Matrix;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
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

    private void addDoubleElement(JPanel panel, String paramName, double max, double min, int y) {

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
    }

    private void addMatrixElement(JPanel panel, String paramName, int maxSize, int minSize, int y) {

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

    private void addElement(JPanel panel, FilterParam element, int y) {
        if (element.type == FieldType.INTEGER) {
            addSimpleElement(panel, element.name, element.max, element.min, element.step, y);
        } else if (element.type == FieldType.DOUBLE){
            addDoubleElement(panel, element.name, element.max, element.min, y);
        } else if (element.type == FieldType.MATRIX) {
            addMatrixElement(panel, element.name, element.max, element.min, y);
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
        setLocationRelativeTo(parent);
    }
}
