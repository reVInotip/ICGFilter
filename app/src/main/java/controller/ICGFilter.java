package controller;

import controller.component.ComponentUpdate;
import controller.filtersWorker.FilterManager;
import model.MainModel;
import model.ModelTasksManager;
import model.tasks.ApplyTask;
import view.MainFrame;

public class ICGFilter {
    private final MainModel model;
    MainFrame mainFrame = MainFrame.create();

    public ICGFilter() {
        model = MainModel.create(mainFrame);
        createToolsButtons();
        if (model == null) {
            throw new RuntimeException("Model is null exception");
        }
    }


    private void createToolsButtons() {
        mainFrame.addToolsButtons(model.getNameFilters(),
                model.getFiltersDescription(), model.getFiltersDescription());
        for (String filter: model.getNameFilters()) {
            mainFrame.addToolActionListener(filter, actionEvent -> MainModel.switchFilter(filter));
        }
    }

    public void start() {
        ComponentUpdate componentUpdate = new ComponentUpdate(model);

        model.start(componentUpdate);
    }
}
