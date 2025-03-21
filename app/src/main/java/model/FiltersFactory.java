package model;

import model.filters.Filter;
import model.filters.IFiltersModel;
import model.filters.filterModels.ModelPrototype;
import objectsFromJson.parsedConfigurationObjects.DialogElement;
import objectsFromJson.parsedConfigurationObjects.ModelConfig;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class FiltersFactory {
    static private final HashMap<String, Class<IFiltersModel>> filters = new HashMap<>();
    static final Map<String, String> filtersDescr = new HashMap<>();
    static final Map<String, String> filtersIcons = new HashMap<>();

    public static void initFactory(HashMap<String, ModelConfig> allInfAtConfig) {
        String name;
        ModelConfig filterData;
        for (Map.Entry<String, ModelConfig> entry: allInfAtConfig.entrySet()) {
            name = entry.getKey();
            filterData = entry.getValue();

            try {
                Class<?> filterClass = Class.forName(filterData.getModelPath());

                if (filterClass.getSuperclass().getName().equals(IFiltersModel.class.getName()) && filterClass.isAnnotationPresent(Filter.class)) {
                    filters.put(name, (Class<IFiltersModel>) filterClass);

                    if (!filterClass.getAnnotation(Filter.class).descr().isEmpty()) {
                        filtersDescr.put(name, filterClass.getAnnotation(Filter.class).descr());
                    }

                    if (!filterClass.getAnnotation(Filter.class).descr().isEmpty()) {
                        filtersIcons.put(name, filterClass.getAnnotation(Filter.class).icon());
                    }

                }
            } catch (ClassNotFoundException e) {
                System.out.println("Can not load class \"" + name + "\" located in path: " +
                        filterData.getModelPath() + " because " + e);
            }
        }
    }

    private static IFiltersModel createFilter(Class<IFiltersModel> filterClass) {
        try {
            return filterClass.getConstructor().newInstance();
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            System.err.println("Class not found");
            throw new RuntimeException(e);
        }
    }

    public static HashMap<String, IFiltersModel> createFilters() {
        HashMap<String, IFiltersModel> filterObjts = new HashMap<>();
        for (Map.Entry<String, Class<IFiltersModel>> filterClass: filters.entrySet()) {
            filterObjts.put(filterClass.getKey(), createFilter(filterClass.getValue()));
        }

        return filterObjts;
    }

    public static ModelPrototype createFilterModel(String name, ModelConfig data) {
        ModelPrototype filterModel = new ModelPrototype(name);
        for (DialogElement element: data.getDialogForm().getElements()) {
            filterModel.addParameter(element.getName(), element.getType(), element.getMin(),
                    element.getMax(), element.getMin(), element.getStep());
        }

        return filterModel;
    }
}
