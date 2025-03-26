package org.example.model;

import dto.FilterDto;
import org.example.event.RepaintEvent;
import org.example.event.StartEvent;
import org.example.event.observers.Observable;
import org.example.event.observers.Observer;
import org.example.model.events.FiltrationCompletedEvent;
import org.example.model.events.ModelEvent;
import org.example.model.events.ModelObserver;
import org.example.model.filters.FilterPrototype;
import org.example.model.filters.filterModels.ModelPrototype;
import org.example.view.MainFrame;

import java.util.HashMap;
import java.util.List;

import java.awt.event.ComponentAdapter;

public class MainModel extends Observable implements ModelObserver {
    public static final int WIDTH = 640;
    public static final int HEIGHT = 480;

    private static boolean isCreated = false;
    private static final ImageWorker imageWorker = new ImageWorker();

    private static String selectedFilter = "def";

    private final MainFrame mainFrame;

    private final HashMap<String, FilterDto> filterDtos;
    private final HashMap<String, String> pathsToFilters;
    private final List<String> filterNames;

    private final HashMap<String, FilterPrototype> filters;
    private final HashMap<String, ModelPrototype> filterModels;

    public static MainModel create(HashMap<String, FilterDto> filterDtos, HashMap<String, String> pathsToFilters, List<String> filterNames) {
        if (!isCreated) {
            isCreated = true;
            return new MainModel(filterDtos, pathsToFilters, filterNames);
        }

        return null;
    }

    private MainModel(HashMap<String, FilterDto> filterDtos, HashMap<String, String> pathsToFilters, List<String> filterNames) {
        this.filterDtos = filterDtos;
        this.filterNames = filterNames;
        this.pathsToFilters = pathsToFilters;

        FiltersFactory.initFactory(pathsToFilters);
        filterModels = FiltersFactory.createFilterModels(filterDtos);
        filters = FiltersFactory.createFilters(filterModels);

        ModelTasksManager.setImageWorker(imageWorker);
        ModelTasksManager.setFilters(filters, this);

        mainFrame = MainFrame.create(filterDtos, filterModels);

        add(mainFrame);

        imageWorker.add(mainFrame);

        assert mainFrame != null;
        for (Observer observer: mainFrame.getInternalObservers()) {
            add(observer);
            imageWorker.add(observer);
        }

        createToolsButtons();
    }

    private void createToolsButtons() {
        mainFrame.addToolsButtons(filterNames, FiltersFactory.filtersDescr, FiltersFactory.filtersIcons);
        for (String filter: filterNames) {
            mainFrame.addToolActionListener(filter, actionEvent -> switchFilter(filter));
        }
    }

    public void start(ComponentAdapter stateChangeAdapter) {
        update(new StartEvent(stateChangeAdapter));
    }

    public void switchFilter(String filter){
        selectedFilter = filter;
    }

    public void stateChanged() {
        update(new RepaintEvent(imageWorker.getFilteredImage()));
    }

    public static String getSelectedFilter(){
        return selectedFilter;
    }

    @Override
    public void update(ModelEvent event) {
        if (event instanceof FiltrationCompletedEvent filtrationCompletedEvent){
            ModelTasksManager.setNewImage(filtrationCompletedEvent.image);
            update(new RepaintEvent(filtrationCompletedEvent.image));
        }
    }
}
