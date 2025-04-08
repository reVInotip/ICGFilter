package org.example.model.filters.filterModels;

import dto.FieldType;

import java.util.List;

public class Parameter {
    public FieldType type;
    public Object parameter;
    public Double max;
    public Double min;
    public Integer step;
    public int index;
    public List<String> link;

    public Parameter(FieldType type, Object parameter, Double max, Double min, Integer step, List<String> link) {
        this.type = type;
        this.parameter = parameter;
        this.max = max;
        this.min = min;
        this.step = step;
        this.link = link;
        index = 0;
    }
}
