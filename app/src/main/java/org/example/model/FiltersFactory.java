package org.example.model;

import dto.FilterDto;
import dto.FilterParam;
import org.example.model.filters.Filter;
import org.example.model.filters.FilterPrototype;
import org.example.model.filters.filterModels.ModelPrototype;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

class FiltersFactory {
    static private final HashMap<String, Class<FilterPrototype>> filters = new HashMap<>();
    static final Map<String, String> filtersDescr = new HashMap<>();
    static final Map<String, String> filtersIcons = new HashMap<>();

    public static void initFactory(HashMap<String, String> pathsToFilters) {
        String filterName;
        String filterPath;
        for (Map.Entry<String, String> entry: pathsToFilters.entrySet()) {
            filterName = entry.getKey();
            filterPath = entry.getValue();

            try {
                Class<?> filterClass = Class.forName(filterPath);

                if (filterClass.getSuperclass().getName().equals(FilterPrototype.class.getName()) && filterClass.isAnnotationPresent(Filter.class)) {
                    filters.put(filterName, (Class<FilterPrototype>) filterClass);

                    if (!filterClass.getAnnotation(Filter.class).descr().isEmpty()) {
                        filtersDescr.put(filterName, filterClass.getAnnotation(Filter.class).descr());
                    }

                    if (!filterClass.getAnnotation(Filter.class).descr().isEmpty()) {
                        filtersIcons.put(filterName, filterClass.getAnnotation(Filter.class).icon());
                    }
                }
            } catch (ClassNotFoundException e) {
                System.out.println("Can not load class \"" + filterName + "\" located in path: " +
                        filterPath + " because " + e);
            }
        }
    }

    private static FilterPrototype createFilter(Class<FilterPrototype> filterClass, ModelPrototype filterModel) {
        try {
            return filterClass.getConstructor(ModelPrototype.class).newInstance(filterModel);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            System.err.println("Class not found " + filterClass + ". Reason: " + e.getCause());
            e.getCause().printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static HashMap<String, FilterPrototype> createFilters(HashMap<String, ModelPrototype> filterModels) {
        HashMap<String, FilterPrototype> filterObjects = new HashMap<>();
        for (Map.Entry<String, Class<FilterPrototype>> filterClass: filters.entrySet()) {
            filterObjects.put(filterClass.getKey(), createFilter(filterClass.getValue(), filterModels.getOrDefault(filterClass.getKey(), null)));
        }
        return filterObjects;
    }

    private static ModelPrototype createFilterModel(String name, FilterDto data) {
        ModelPrototype filterModel = new ModelPrototype(name);
        FilterParam param;
        for (Map.Entry<String, FilterParam> filterParam: data.getFilterParams().entrySet()) {
            param = filterParam.getValue();
            filterModel.addParameter(param.name, param.type, param.getMinorParamsList());
        }

        return filterModel;
    }

    public static HashMap<String, ModelPrototype> createFilterModels(HashMap<String, FilterDto> filterDtos) {
        HashMap<String, ModelPrototype> filterModels = new HashMap<>();
        for (Map.Entry<String, FilterDto> filterDtoEntry: filterDtos.entrySet()) {
            filterModels.put(filterDtoEntry.getKey(), createFilterModel(filterDtoEntry.getKey(), filterDtoEntry.getValue()));
        }

        return filterModels;
    }
}
