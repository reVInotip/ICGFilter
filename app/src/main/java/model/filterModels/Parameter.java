package model.filterModels;

public class Parameter {
    public String type;
    public Object parameter;
    public int max;
    public int min;
    public Integer step;

    public Parameter(String type, Object parameter, int max, int min, Integer step) {
        this.type = type;
        this.parameter = parameter;
        this.max = max;
        this.min = min;
        this.step = step;
    }
}
