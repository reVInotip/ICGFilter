package org.example.model;

import org.example.event.RepaintEvent;
import org.example.model.filters.FilterPrototype;
import org.example.model.tasks.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        taskList.removeLast(); // FIX THIS!!!
      
        switch (currTask) {
            case LoadTask loadTask ->
                    imageWorker.load(loadTask.imagePath, loadTask.imageName);

            case SaveTask saveTask ->
                    imageWorker.save(saveTask.imagePath, saveTask.imageName);

            case ApplyTask applyTask -> {
                    if (imageWorker.getLoadedImage() == null || imageWorker.getFilteredImage() == null) {
                        System.err.println("Image is null");
                        return -1;
                    } else if (!filters.containsKey(applyTask.filterName)) {
                        System.err.println("Filter not chosen");
                        return -1;
                    }
                    filters.get(applyTask.filterName).convert(
                            imageWorker.getLoadedImage(),
                            imageWorker.getFilteredImage()
                    );
            }

            case FullScreenTask fullScreenTask ->
                    model.imgToFullScreen();

            case ReturnToOriginalImgTask returnToOriginalImgTask ->
                    model.update(new RepaintEvent(imageWorker.getLoadedImage()));

            case ReturnToFilteredImgTask returnToFilteredImgTask ->
                    model.update(new RepaintEvent(imageWorker.getFilteredImage()));

            default ->
                    throw new IllegalArgumentException("Unknown task type: " + currTask.getClass());
        }

        return 0;
    }

}
