package org.example.controller;

import dto.FilterDto;
import org.example.controller.component.ComponentUpdate;
import org.example.controller.component.PanelMouseAdapter;
import org.example.model.MainModel;

import java.util.HashMap;
import java.util.List;

public class ICGFilter {
    private final MainModel model;

    public ICGFilter(HashMap<String, FilterDto> filterDtos, HashMap<String, String> pathsToFilters, List<String> filterNames) {
        model = MainModel.create(filterDtos, pathsToFilters, filterNames);
    }

    public void start() {
        ComponentUpdate componentUpdate = new ComponentUpdate(model);

        PanelMouseAdapter panelMouseAdapter = new PanelMouseAdapter(model);



        model.start(componentUpdate, panelMouseAdapter);
    }
}
