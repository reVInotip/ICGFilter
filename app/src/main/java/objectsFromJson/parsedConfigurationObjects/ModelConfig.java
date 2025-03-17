package objectsFromJson.parsedConfigurationObjects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ModelConfig {
    @JsonProperty("modelPath")
    private String modelPath;
    @JsonProperty("dialogFormExist")
    private boolean dialogFormExist;
    @JsonProperty("dialogFormConfig")
    private DialogFormConfig dialogFormConfig;

    // Геттеры и сеттеры
    public String getModelPath() {
        return modelPath;
    }

    public void setModelPath(String modelPath) {
        this.modelPath = modelPath;
    }

    public boolean isDialogFormExist() {
        return dialogFormExist;
    }

    public void setDialogFormExist(boolean dialogFormExist) {
        this.dialogFormExist = dialogFormExist;
    }

    public DialogFormConfig getDialogForm() {
        return dialogFormConfig;
    }

    public void setDialogForm(DialogFormConfig dialogFormConfig) {
        this.dialogFormConfig = dialogFormConfig;
    }

    @Override
    public String toString() {
        return "ModelConfig{" +
                "ModelPath='" + modelPath + '\'' +
                ", dialogFormExist=" + dialogFormExist +
                ", dialogFormConfig=" + dialogFormConfig +
                '}';
    }
}