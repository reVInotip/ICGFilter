package org.example.model.filters.filterModels;

import dto.FieldType;
import org.example.model.filters.filterModels.customTypes.Matrix;
import org.example.model.filters.filterModels.customTypes.MatrixData;
import org.example.model.filters.filterModels.events.FilterModelObservable;
import org.example.utils.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

public class ModelPrototype extends FilterModelObservable {
    private final HashMap<String, Parameter> parameters = new HashMap<>();
    private final HashMap<String, Parameter> runtimeParameters = new HashMap<>();
    private final String name;

    public ModelPrototype(String name) {
        this.name = name;
    }

    public ModelPrototype(String name, HashMap<String, Parameter> parameters) {
        this.name = name;
        this.parameters.putAll(parameters);
    }

    public void configure(HashMap<String, Parameter> parameters) {
        this.parameters.putAll(parameters);
    }

    public void addRuntimeParameter(String name, FieldType type, List<Object> minorParams) {
        if (minorParams == null) {
            throw new RuntimeException("Invalid parameter");
        }

        switch (type) {
            case INTEGER, DOUBLE, LIST, MATRIX_DATA -> {
                // add logic
            }
            case MATRIX -> {
                var data = new Pair<Matrix, Integer>(
                        new Matrix((int[]) minorParams.getFirst(), (int) minorParams.get(1), (int) minorParams.get(1)),
                        (int) minorParams.get(2));
                runtimeParameters.put(name, new Parameter(
                        type,
                        data,
                        (int) minorParams.get(1),
                        (int) minorParams.get(1),
                        null,
                        null)
                );
            }
            default -> {
                throw new RuntimeException("Invalid type");
            }
        }
    }

    public Parameter getRuntimeParameter(String name) {
        if (!runtimeParameters.containsKey(name)) {
            throw new RuntimeException("Invalid parameter");
        }

        return runtimeParameters.get(name);
    }

    public void addParameter(String name, FieldType type, List<Object> minorParams) {
        if (minorParams == null) {
            throw new RuntimeException("Invalid parameter");
        }

        switch (type) {
            case INTEGER, DOUBLE -> {
                parameters.put(name, new Parameter(
                        type,
                        minorParams.getFirst(),
                        (int) minorParams.get(1),
                        (int) minorParams.getFirst(),
                        (Integer) minorParams.get(2), //step may be null,
                        null)
                );
            }
            case MATRIX -> {
                var data = new Matrix((int) minorParams.getFirst(), (int) minorParams.getFirst());
                parameters.put(name, new Parameter(
                        type,
                        data,
                        (int) minorParams.get(1),
                        (int) minorParams.getFirst(),
                        null,
                        null)
                );
            }
            case LIST -> {
                if (minorParams.getFirst() instanceof List<?> elements &&
                    minorParams.get(1) instanceof List<?> link) {
                    parameters.put(name, new Parameter(
                            type,
                            (List<String>) elements,
                            0,
                            elements.size(),
                            null,
                            (List<String>) link
                    ));
                }
            }
            case MATRIX_DATA -> {
                if (minorParams.getFirst() instanceof List<?> data) {
                    int min = data.stream().mapToInt(x -> (int) x).min().orElseThrow(NoSuchElementException::new);
                    int max = data.stream().mapToInt(x -> (int) x).max().orElseThrow(NoSuchElementException::new);

                    var matrixData = new MatrixData(min, min, min, (List<Integer>) data);
                    parameters.put(name, new Parameter(
                            type,
                            matrixData,
                            max,
                            min,
                            null,
                            null)
                    );
                }
            }
            default -> {
                throw new RuntimeException("Invalid type");
            }
        }

    }

    public boolean isMatrix(String name) {
        return parameters.containsKey(name) && parameters.get(name).type == FieldType.MATRIX;
    }

    public boolean isDouble(String name) {
        return parameters.containsKey(name) && parameters.get(name).type == FieldType.DOUBLE;
    }

    public boolean isInteger(String name) {
        return parameters.containsKey(name) && parameters.get(name).type == FieldType.INTEGER;
    }

    public List<String> getLinkElements(String name) {
        if (!parameters.containsKey(name)) {
            throw new RuntimeException("Invalid parameter");
        }

        return parameters.get(name).link;
    }

    public void setListElement(String name, int index) {
        if (!parameters.containsKey(name) || (parameters.get(name).type != FieldType.LIST) ||
                index < parameters.get(name).min || index > parameters.get(name).max) {
            throw new RuntimeException("Invalid parameter");
        }

        parameters.get(name).index = index;
    }

    public String getListElement(String name) {
        if (parameters.containsKey(name) && (parameters.get(name).type == FieldType.LIST)) {
            return ((List<String>) parameters.get(name).parameter).get(parameters.get(name).index);
        }
        else {
            throw new RuntimeException("Invalid parameter");
        }
    }

    public void setInteger(String name, int value) {
        if (!parameters.containsKey(name) || (parameters.get(name).type != FieldType.INTEGER) ||
                value < parameters.get(name).min || value > parameters.get(name).max) {
            throw new RuntimeException("Invalid parameter");
        }

        parameters.get(name).parameter = value;
    }

    public void setMatrix(String name, int x, int y, int value) {
        ((Matrix) parameters.get(name).parameter).set(x, y, value);
    }

    public Integer getInteger(String name) {
        if (parameters.containsKey(name) && (parameters.get(name).type == FieldType.INTEGER)) {
             return (Integer) parameters.get(name).parameter;
        }
        else {
            throw new RuntimeException("Invalid parameter");
        }
    }

    public Double getDouble(String name) {
        if (parameters.containsKey(name) && (parameters.get(name).type == FieldType.DOUBLE)) {
            return (Double) parameters.get(name).parameter;
        }
        else {
            throw new RuntimeException("Invalid parameter");
        }
    }

    public Matrix getMatrix(String name) {
        if (parameters.containsKey(name) && (parameters.get(name).type == FieldType.MATRIX)
                && parameters.get(name).parameter instanceof Matrix) {
            return (Matrix) parameters.get(name).parameter;
        }
        else {
            throw new RuntimeException("Invalid parameter");
        }
    }

    public MatrixData getMatrixData(String name) {
        if (parameters.containsKey(name) && (parameters.get(name).type == FieldType.MATRIX_DATA)
                && parameters.get(name).parameter instanceof MatrixData) {
            return (MatrixData) parameters.get(name).parameter;
        }
        else {
            throw new RuntimeException("Invalid parameter");
        }
    }

    public void setMatrixData(String name, int currSize, String channel) {
        if (!parameters.containsKey(name) || (parameters.get(name).type != FieldType.MATRIX_DATA)
                || !(parameters.get(name).parameter instanceof MatrixData) ||
                (currSize < parameters.get(name).min || currSize > parameters.get(name).max) ||
                channel == null) {
            throw new RuntimeException("Invalid parameter");
        }

        if (channel.equals("r") || channel.equals("red")) {
            ((MatrixData) parameters.get(name).parameter).setCurrSizeForRedChannel(currSize);
        } else if (channel.equals("g") || channel.equals("green")) {
            ((MatrixData) parameters.get(name).parameter).setCurrSizeForGreenChannel(currSize);
        } else if (channel.equals("b") || channel.equals("blue")) {
            ((MatrixData) parameters.get(name).parameter).setCurrSizeForBlueChannel(currSize);
        } else {
            throw new RuntimeException("Invalid parameter");
        }
    }

    public String getName() {
        return name;
    }

    public void setDouble(String paramName, double value) {
        if (!parameters.containsKey(paramName) || (parameters.get(paramName).type != FieldType.DOUBLE) ||
                value < (double)parameters.get(paramName).min || value > (double)parameters.get(paramName).max) {
            throw new RuntimeException("Invalid parameter");
        }
        parameters.get(paramName).parameter = value;
    }
}
