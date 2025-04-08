package jsonParser.parsedConfigurationObjects;

import com.fasterxml.jackson.annotation.JsonProperty;
import dto.FieldType;

import java.util.List;

public class DialogElement {
    private String name;
    private FieldType type;
    private Double max;
    private Double min;
    @JsonProperty("size")
    private List<Integer> size;
    private Integer step; //Integer, так как поле может отсутствовать
    @JsonProperty("elements")
    private List<String> elements;
    @JsonProperty("link")
    private List<String> link;

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

    public Double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public Double getMin() {
        return min;
    }

    public void setMin(double min) {
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

    public void setElements(List<String> elements) {
        this.elements = elements;
    }

    public List<String> getElements() {
        return elements;
    }

    public List<String> getLink() {
        return link;
    }

    public void setLink(List<String> link) {
        this.link = link;
    }
}