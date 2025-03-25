package jsonParser.parsedConfigurationObjects;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class FilterConfig {
    @JsonProperty("filterPath")
    private String filterPath;
    @JsonProperty("dialogWindowExist")
    private boolean dialogWindowExist;
    @JsonProperty("filterParams")
    private List<DialogElement> filterParams;

    // Геттеры и сеттеры
    public String getFilterPath() {
        return filterPath;
    }

    public void setFilterPath(String filterPath) {
        this.filterPath = filterPath;
    }

    public boolean isDialogWindowExist() {
        return dialogWindowExist;
    }

    public void setDialogWindowExist(boolean dialogWindowExist) {
        this.dialogWindowExist = dialogWindowExist;
    }

    public List<DialogElement> getFilterParams() {
        return filterParams;
    }

    @Override
    public String toString() {
        return "FilterConfig{" +
                "ModelPath='" + filterPath + '\'' +
                ", dialogFormExist=" + dialogWindowExist +
                '}';
    }
}