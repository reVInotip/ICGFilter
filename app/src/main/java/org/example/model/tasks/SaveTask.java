package org.example.model.tasks;

public class SaveTask implements Task {
    public String imagePath;
    public String imageName;

    public SaveTask(String imagePath, String imageName) {
        this.imageName = imageName;
        this.imagePath = imagePath;
    }
}
