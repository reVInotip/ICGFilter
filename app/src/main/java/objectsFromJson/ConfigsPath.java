package objectsFromJson;

import java.util.List;


import com.fasterxml.jackson.annotation.JsonProperty;

public class ConfigsPath {
    @JsonProperty("ConfigPath") // Указываем, что поле JSON "ConfigPath" соответствует этому полю
    private List<ConfigPath> ConfigPath;

    public List<ConfigPath> getToolPath() {
        return ConfigPath;
    }

    public void setToolPath(List<ConfigPath> configPath) {
        this.ConfigPath = configPath;
    }

    @Override
    public String toString() {
        return "ConfigsPath{" +
                "ConfigPath=" + ConfigPath +
                '}';
    }
}