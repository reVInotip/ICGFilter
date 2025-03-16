package objectsFromJson.parsedConfigurationObjects;

public class DialogElement {
    private String name;
    private String type;
    private int max;
    private int min;
    private Integer step; //Integer, так как поле может отсутствовать

    // Геттеры и сеттеры
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
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