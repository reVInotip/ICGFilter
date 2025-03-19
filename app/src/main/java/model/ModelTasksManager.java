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

// задел под ассинхронность (тред пул)
public class ModelTasksManager implements ModelObserver {
    private static final List<Task> taskList = new ArrayList<>();
    private static ImageWorker imageWorker;

    private static HashMap<String, FiltersModel> filters = null;

    private static MainModel model;

    static void setImageWorker(ImageWorker imageW) {
        imageWorker = imageW;
    }

    static void setFilters(HashMap<String, FiltersModel> filters) {
        ModelTasksManager.filters = filters;
    }

    static void setModel(MainModel model) {
        ModelTasksManager.model = model;
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
        } else if (currTask instanceof ApplyTask applyTask) {
            BufferedImage currentImage = imageWorker.getImage();
            filters.get(applyTask.filterName).convert(currentImage);
        }
    }

    public void update(ModelEvent event) {
        if (event instanceof FiltrationCompletedEvent){
            FiltrationCompletedEvent filtrationCompletedEvent = (FiltrationCompletedEvent) event;
            model.SendEvent(new RepaintEvent(filtrationCompletedEvent.image));
        }
    }
}
