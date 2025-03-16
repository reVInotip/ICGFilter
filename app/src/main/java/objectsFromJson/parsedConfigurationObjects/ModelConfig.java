package objectsFromJson.parsedConfigurationObjects;

public class ModelConfig {
    private String ModelPath;
    private boolean dialogFormExist;
    private DialogFormConfig dialogFormConfig;

    // Геттеры и сеттеры
    public String getModelPath() {
        return ModelPath;
    }

    public void setModelPath(String modelPath) {
        this.ModelPath = modelPath;
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
                "ModelPath='" + ModelPath + '\'' +
                ", dialogFormExist=" + dialogFormExist +
                ", dialogFormConfig=" + dialogFormConfig +
                '}';
    }
}