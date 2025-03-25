package dto;

public class FilterParam {
    public FieldType type;
    public int max;
    public int min;
    public Integer step;
    public String name;

    public FilterParam(String name, FieldType type, int max, int min, Integer step) {
        this.type = type;
        this.max = max;
        this.min = min;
        this.step = step;
        this.name = name;
    }
}
