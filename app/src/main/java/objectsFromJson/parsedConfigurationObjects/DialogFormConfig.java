package objectsFromJson.parsedConfigurationObjects;

import java.util.List;

public class DialogFormConfig {
    private String name;
    private List<DialogElement> elements;

    // Геттеры и сеттеры
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<DialogElement> getElements() {
        return elements;
    }

    public void setElements(List<DialogElement> elements) {
        this.elements = elements;
    }

    @Override
    public String toString() {
        return "DialogFormConfig{" +
                "name='" + name + '\'' +
                ", elements=" + elements +
                '}';
    }
}