package model;

import model.tasks.LoadTask;
import model.tasks.SaveTask;
import model.tasks.Task;

import java.util.ArrayList;
import java.util.List;

// задел под ассинхронность (тред пул)
public class ModelTasksManager {
    private static final List<Task> taskList = new ArrayList<>();
    private static ImageWorker imageWorker;

    static void setImageWorker(ImageWorker imageW) {
        imageWorker = imageW;
    }

    public static void addTask(Task task) {
        taskList.add(task);
        run();
    }

    public static void run() {
        Task currTask = taskList.getFirst();

        if (currTask instanceof LoadTask loadTask) {
            imageWorker.load(loadTask.imagePath, loadTask.imageName);
        } else if (currTask instanceof SaveTask saveTask) {
            imageWorker.save(saveTask.imagePath, saveTask.imageName);
        }
    }
}
