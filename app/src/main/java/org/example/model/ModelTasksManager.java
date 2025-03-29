package org.example.model;

import org.example.model.filters.FilterPrototype;
import org.example.model.tasks.ApplyTask;
import org.example.model.tasks.LoadTask;
import org.example.model.tasks.SaveTask;
import org.example.model.tasks.Task;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// задел под ассинхронность (тред пул)
public class ModelTasksManager {
    private static final List<Task> taskList = new ArrayList<>();
    private static ImageWorker imageWorker;

    private static HashMap<String, FilterPrototype> filters = null;

    static void setImageWorker(ImageWorker imageW) {
        imageWorker = imageW;
    }

    static void setFilters(HashMap<String, FilterPrototype> filters, MainModel model) {
        ModelTasksManager.filters = filters;
        for (Map.Entry<String, FilterPrototype> entry : filters.entrySet()) {
            FilterPrototype value = entry.getValue();
            value.add(model);
        }

    }

    public static void addTask(Task task) {
        taskList.add(task);
        run();
    }

    public static void run() {
        Task currTask = taskList.getFirst();

        taskList.removeLast();

        if (currTask instanceof LoadTask loadTask) {
            imageWorker.load(loadTask.imagePath, loadTask.imageName);
        } else if (currTask instanceof SaveTask saveTask) {
            imageWorker.save(saveTask.imagePath, saveTask.imageName);
        } else if (currTask instanceof ApplyTask applyTask) {
            filters.get(applyTask.filterName).convert(imageWorker.getLoadedImage(), imageWorker.getFilteredImage());
        }
    }

}
