package model.filters.filterModels;

import model.filters.filterModels.customTypes.Matrix;

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

    public void addParameter(String name, String type, Object data, int max, int min, Integer step) {
        parameters.put(name, new Parameter(type, data, max, min, step));
    }

    public Integer getInteger(String name) {
        if (parameters.containsKey(name) && parameters.get(name).type.equals("int")) {
             return (Integer) parameters.get(name).parameter;
        }

        return null;
    }

    public Matrix getMatrix(String name) {
        if (parameters.containsKey(name) && parameters.get(name).type.equals("matrix")) {
            return (Matrix) parameters.get(name).parameter;
        }

        return null;
    }
}
