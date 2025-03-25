package org.example.view.components;

import dto.FilterDto;
import org.example.model.filters.filterModels.ModelPrototype;
import org.example.view.components.filterDialogs.DialogPrototype;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

class DialogsFactory {
    private static final HashMap<FilterDto, ModelPrototype> dialogForms = new HashMap<>();

    public static HashMap<String, DialogPrototype> createDialogs(JFrame parent) {
        final HashMap<String, DialogPrototype> dialogs = new HashMap<>();
        for (HashMap.Entry<FilterDto, ModelPrototype> dialogForm: dialogForms.entrySet()) {
            dialogs.put(dialogForm.getValue().getName(), new DialogPrototype(
                    parent,
                    dialogForm.getKey().getName(),
                    dialogForm.getKey().getFilterParams(),
                    dialogForm.getValue()));
        }

        return dialogs;
    }

    public static void initFactory(HashMap<String, FilterDto> filterDtos, HashMap<String, ModelPrototype> filterModels) {
        for (Map.Entry<String, FilterDto> filterDtoEntry: filterDtos.entrySet()) {
            if (filterModels.containsKey(filterDtoEntry.getKey())) {
                dialogForms.put(filterDtoEntry.getValue(), filterModels.get(filterDtoEntry.getKey()));
            }
        }
    }
}
