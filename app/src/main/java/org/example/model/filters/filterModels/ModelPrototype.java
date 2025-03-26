package org.example.model.filters.filterModels;

import dto.FieldType;
import org.example.model.filters.filterModels.customTypes.Matrix;

import java.util.HashMap;

public class ModelPrototype {
    private final HashMap<String, Parameter> parameters = new HashMap<>();
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

    public void addParameter(String name, FieldType type, int max, int min, Integer step) {
        Object data;
        switch (type) {
            case INTEGER -> {
                data = min;
            }
            case DOUBLE -> {
                data = (double) min;
            }
            case MATRIX -> {
                data = new Matrix(min, min);
            }
            default -> {
                throw new RuntimeException("Invalid type");
            }
        }
        parameters.put(name, new Parameter(type, data, max, min, step));
    }

    public void setInteger(String name, int value) {
        if (value < parameters.get(name).min || value > parameters.get(name).max) {
            throw new RuntimeException("Invalid parameter");
        }

        parameters.get(name).parameter = value;
    }

    public void setMatrix(String name, Matrix value) {
        parameters.get(name).parameter = value;
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

    public String getName() {
        return name;
    }

    public void setDouble(String paramName, double value) {
        if (value < (double)parameters.get(paramName).min || value > (double)parameters.get(paramName).max) {
            throw new RuntimeException("Invalid parameter");
        }
        parameters.get(paramName).parameter = value;
    }
}
