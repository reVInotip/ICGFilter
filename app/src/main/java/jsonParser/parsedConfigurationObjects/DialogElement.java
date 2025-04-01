package jsonParser.parsedConfigurationObjects;

import com.fasterxml.jackson.annotation.JsonProperty;
import dto.FieldType;

import java.util.List;

public class DialogElement {
    private String name;
    private FieldType type;
    private Integer max;
    private Integer min;
    @JsonProperty("size")
    private List<Integer> size;
    private Integer step; //Integer, так как поле может отсутствовать

    // Геттеры и сеттеры
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FieldType getType() {
        return type;
    }

    public void setType(FieldType type) {
        this.type = type;
    }

    public Integer getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public Integer getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public List<Integer> getSize() {
        return size;
    }

    public void setSize(List<Integer> size) {
        this.size = size;
    }

    public Integer getStep() {
        return step;
    }

    public void setStep(Integer step) {
        this.step = step;
    }

    @Override
    public String toString() {
        return "DialogElement{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", max=" + max +
                ", min=" + min +
                ", step=" + step +
                '}';
    }
}