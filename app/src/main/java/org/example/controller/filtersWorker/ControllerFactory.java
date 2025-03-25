package org.example.controller.filtersWorker;

import org.example.controller.filters.IFilter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


// не нужна по идеи
// т.к есле следовать из того что просчёт матриц происходит в модели
// все контролеры будут одинаковы, но просто будут кидать разные ивенты или что-то такое.
// этим я хочу сказать, что можно это всё запихать в FilterManager в метод switchFilter(привел пример реализации)
//
public class ControllerFactory {
    static private final Map<String, Class<IFilter>> toolClasses = new HashMap<>();
    static public void initFactory(List<String> filtersPaths){
        filtersPaths.forEach(classPath -> {
            try {
                Class<?> tool = Class.forName(classPath);

            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
