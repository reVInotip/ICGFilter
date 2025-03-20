package model;

import Factory.MainFactory;
import event.Event;
import event.RepaintEvent;
import event.StartEvent;
import event.observers.Observable;
import event.observers.Observer;
import model.events.FiltrationCompletedEvent;
import model.events.ModelEvent;
import model.events.ModelObserver;
import model.filters.FiltersModel;
import view.MainFrame;

import java.util.HashMap;
import java.util.Map;

import java.awt.event.ComponentAdapter;

public class MainModel extends Observable implements ModelObserver {
    public static final int WIDTH = 640;
    public static final int HEIGHT = 480;

    private static boolean isCreated = false;
    private static final ImageWorker imageWorker = new ImageWorker();

    private static String selectedFilter = "def";

    private static HashMap<String, FiltersModel> filters = MainFactory.createModels();

    public static MainModel create(MainFrame mainFrame) {
        if (!isCreated) {
            isCreated = true;
            return new MainModel(mainFrame);
        }

        return null;
    }

    private MainModel(MainFrame mainFrame) {
        ModelTasksManager.setImageWorker(imageWorker);
        ModelTasksManager.setFilters(filters, this);

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

    public void switchFilter(String filter){
        selectedFilter = filter;
    }

    public void stateChanged() {
        update(new RepaintEvent(imageWorker.getLoadedImage()));
    }

    public void SendEvent(Event event){
        update(event);
    }

    public Map<String, String> getFiltersDescription() {
        return FiltersFactory.filtersDescr;
    }

    public Map<String, String> getFiltersIcons() {
        return FiltersFactory.filtersIcons;
    }

    public static String getSelectedFilter(){
        return selectedFilter;
    }

    public String[] getNameFilters() {
        return filters.keySet().toArray(new String[0]);
    }

    @Override
    public void update(ModelEvent event) {
        if (event instanceof FiltrationCompletedEvent){
            FiltrationCompletedEvent filtrationCompletedEvent = (FiltrationCompletedEvent) event;
            ModelTasksManager.setNewImage(filtrationCompletedEvent.image);
            update(new RepaintEvent(filtrationCompletedEvent.image));
        }
    }
}
