package model;

import event.RepaintEvent;
import event.StartEvent;
import event.observers.Observable;
import event.observers.Observer;
import view.MainFrame;
import java.util.Map;

import java.awt.event.ComponentAdapter;

public class MainModel extends Observable {
    public static final int WIDTH = 640;
    public static final int HEIGHT = 480;

    private static boolean isCreated = false;
    private static final ImageWorker imageWorker = new ImageWorker();

    public static MainModel create() {
        if (!isCreated) {
            isCreated = true;
            ModelTasksManager.setImageWorker(imageWorker);
            return new MainModel();
        }

        return null;
    }

    private MainModel() {
        MainFrame mainFrame = MainFrame.create();
        add(mainFrame);
        imageWorker.add(mainFrame);

        assert mainFrame != null;
        for (Observer observer: mainFrame.getInternalObservers()) {
            add(observer);
            imageWorker.add(observer);
        }
    }

    public void start(ComponentAdapter stateChangeAdapter) {
        update(new StartEvent(stateChangeAdapter));
    }

    public void stateChanged() {
        update(new RepaintEvent(imageWorker.getImage()));
    }

    public Map<String, String> getFiltersDescription() {
        return FiltersFactory.filtersDescr;
    }

    public Map<String, String> getFiltersIcons() {
        return FiltersFactory.filtersIcons;
    }
}
