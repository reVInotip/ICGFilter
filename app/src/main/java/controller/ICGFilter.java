package controller;

import controller.filtersWorker.FilterManager;
import model.MainModel;

public class ICGFilter {
    private final FilterManager filterManager = new FilterManager();
    private final MainModel model = MainModel.create();

    public ICGFilter() {
        //createToolsButtons();
        if (model == null) {
            throw new RuntimeException("Model is null exception");
        }
    }


    private void createToolsButtons() {
//        mainFrame.addToolsButtons(toolManager.getAvailableTools().toArray(new String[0]),
//                toolManager.getToolsDescription(), toolManager.getToolsIcons());
//        for (String tool: toolManager.getAvailableTools()) {
//            mainFrame.addToolActionListener(tool, actionEvent -> toolManager.switchTool(tool));
//        }
    }

    public void start() {
        model.start();
    }
}
