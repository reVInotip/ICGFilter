package controller;

import controller.filtersWorker.FilterManager;

public class ICGFilter {
    //private MainForm mainForm;
    private final FilterManager filterManager = new FilterManager();

    ICGFilter(){
        createToolsButtons();

    }


    private void createToolsButtons() {
//        mainFrame.addToolsButtons(toolManager.getAvailableTools().toArray(new String[0]),
//                toolManager.getToolsDescription(), toolManager.getToolsIcons());
//        for (String tool: toolManager.getAvailableTools()) {
//            mainFrame.addToolActionListener(tool, actionEvent -> toolManager.switchTool(tool));
//        }
    }

}
