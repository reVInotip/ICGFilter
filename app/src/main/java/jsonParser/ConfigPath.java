package jsonParser;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ConfigPath {
    @JsonProperty("name")
    private String filterName;
    private String path;

    public String getFilterName() {
        return filterName;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "Tool{" +
                "name='" + filterName + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}