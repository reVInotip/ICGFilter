package model.tasks;

public class LoadTask implements Task {
    public String imagePath;
    public String imageName;

    public LoadTask(String imagePath, String imageName) {
        this.imageName = imageName;
        this.imagePath = imagePath;
    }
}
