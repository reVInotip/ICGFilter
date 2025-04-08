package org.example.model;

import org.example.model.filters.FilterPrototype;
import org.example.model.tasks.ApplyTask;
import org.example.model.tasks.LoadTask;
import org.example.model.tasks.SaveTask;
import org.example.model.tasks.FullScreenTask;
import org.example.model.tasks.Task;

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

    private static MainModel model;

    static void setFilters(HashMap<String, FilterPrototype> filters, MainModel model) {

        ModelTasksManager.model = model;

        ModelTasksManager.filters = filters;
        for (Map.Entry<String, FilterPrototype> entry : filters.entrySet()) {
            FilterPrototype value = entry.getValue();
            value.add(model);
        }

    }

    public static int addTask(Task task) {
        taskList.add(task);
        return run();
    }

    public static int run() {
        Task currTask = taskList.getFirst();

        taskList.removeLast();

        if (currTask instanceof LoadTask loadTask) {
            imageWorker.load(loadTask.imagePath, loadTask.imageName);
        } else if (currTask instanceof SaveTask saveTask) {
            imageWorker.save(saveTask.imagePath, saveTask.imageName);
        } else if (currTask instanceof ApplyTask applyTask) {
            if (imageWorker.getLoadedImage() == null || imageWorker.getFilteredImage() == null) {
                System.err.println("Image is null");
                return -1;
            } else if (!filters.containsKey(applyTask.filterName)) {
                System.err.println("Filter not chosen");
                return -1;
            }
            filters.get(applyTask.filterName).convert(imageWorker.getLoadedImage(), imageWorker.getFilteredImage());
        } else if (currTask instanceof FullScreenTask fullScreenTask) {
            model.imgToFullScreen();
        }

        return 0;
    }

}
