package org.example.model.filters.filterModels;

import dto.FieldType;

public class Parameter {
    public FieldType type;
    public Object parameter;
    public int max;
    public int min;
    public Integer step;

    public Parameter(FieldType type, Object parameter, int max, int min, Integer step) {
        this.type = type;
        this.parameter = parameter;
        this.max = max;
        this.min = min;
        this.step = step;
    }
}
