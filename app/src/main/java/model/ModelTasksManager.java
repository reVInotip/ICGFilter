package model;

import event.RepaintEvent;
import model.events.FiltrationCompletedEvent;
import model.events.ModelEvent;
import model.events.ModelObserver;
import model.filters.FiltersModel;
import model.tasks.ApplyTask;
import model.tasks.LoadTask;
import model.tasks.SaveTask;
import model.tasks.Task;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// задел под ассинхронность (тред пул)
public class ModelTasksManager {
    private static final List<Task> taskList = new ArrayList<>();
    private static ImageWorker imageWorker;

    private static HashMap<String, FiltersModel> filters = null;

    static void setImageWorker(ImageWorker imageW) {
        imageWorker = imageW;
    }

    static void setFilters(HashMap<String, FiltersModel> filters, MainModel model) {
        ModelTasksManager.filters = filters;
        for (Map.Entry<String, FiltersModel> entry : filters.entrySet()) {
            FiltersModel value = entry.getValue();
            value.add(model);
        }

    }

    public static void addTask(Task task) {
        taskList.add(task);
        run();
    }

    public static void run() {
        Task currTask = taskList.getFirst();

        taskList.remove(taskList.size() - 1);

        if (currTask instanceof LoadTask loadTask) {
            imageWorker.load(loadTask.imagePath, loadTask.imageName);
        } else if (currTask instanceof SaveTask saveTask) {
            imageWorker.save(saveTask.imagePath, saveTask.imageName);
        } else if (currTask instanceof ApplyTask applyTask) {
            BufferedImage currentImage = imageWorker.getLoadedImage();
            filters.get(applyTask.filterName).convert(currentImage);
        }
    }

    public static void setNewImage(BufferedImage currentImage){
        imageWorker.setImage(currentImage);
    }

}
