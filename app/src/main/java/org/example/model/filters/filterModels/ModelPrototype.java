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

    public void addParameter(String name, FieldType type, Object data, int max, int min, Integer step) {
        parameters.put(name, new Parameter(type, data, max, min, step));
    }

    public void setInteger(String name, int value) {
        if (value < parameters.get(name).min || value > parameters.get(name).max) {
            throw new RuntimeException("Invalid parameter");
        }

        parameters.get(name).parameter = value;
    }

    public Integer getInteger(String name) {
        if (parameters.containsKey(name) && (parameters.get(name).type == FieldType.INTEGER)) {
             return (Integer) parameters.get(name).parameter;
        }

        return null;
    }

    public Matrix getMatrix(String name) {
        if (parameters.containsKey(name) && (parameters.get(name).type == FieldType.MATRIX)) {
            return (Matrix) parameters.get(name).parameter;
        }

        return null;
    }

    public String getName() {
        return name;
    }
}
