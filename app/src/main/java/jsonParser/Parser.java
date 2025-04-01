package jsonParser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import dto.FilterDto;
import dto.FilterParam;
import jsonParser.parsedConfigurationObjects.DialogElement;
import jsonParser.parsedConfigurationObjects.FilterConfig;
import org.example.App;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Parser {
    static private final String RESOURCE_FILE_NAME  = "/filtersPathToConfig.json";
    private final HashMap<String, FilterDto> filterDtos = new HashMap<>();
    private final HashMap<String, String> pathsToFilters = new HashMap<>();
    private final List<String> filterNames = new ArrayList<>();

    public void parse() {
        ConfigsPath filtersConfigPath = null;
        ObjectMapper mapper = new ObjectMapper();

        //для получения путей до config этих фильтров
        try {
            InputStream inputStream = App.class.getResourceAsStream(RESOURCE_FILE_NAME);
            if (inputStream == null) {
                throw new RuntimeException("Файл не найден: " + RESOURCE_FILE_NAME);
            }

            filtersConfigPath = mapper.readValue(inputStream, ConfigsPath.class);

        } catch (Exception e) {
            e.printStackTrace();
        }

        for (ConfigPath configPath : filtersConfigPath.getToolPath()) {
            try {
                InputStream inputStream = App.class.getResourceAsStream(configPath.getPath());

                if (inputStream == null) {
                    throw new RuntimeException("Файл не найден: " + configPath.getPath());
                }

                // Парсим JSON в объект
                FilterConfig filterConfig = mapper.readValue(inputStream, FilterConfig.class);

                if (filterConfig.isDialogWindowExist()) {
                    FilterDto filterDto = new FilterDto(configPath.getFilterName());
                    for (DialogElement dialogElement: filterConfig.getFilterParams()) {
                        filterDto.addFilterParam(dialogElement.getName(), new FilterParam(
                                dialogElement.getName(),
                                dialogElement.getType(),
                                dialogElement.getMax(),
                                dialogElement.getMin(),
                                dialogElement.getSize(),
                                dialogElement.getStep()
                        ));
                    }

                    filterDtos.put(configPath.getFilterName(), filterDto);
                }

                filterNames.add(configPath.getFilterName());
                pathsToFilters.put(configPath.getFilterName(), filterConfig.getFilterPath());
                System.out.println(filterConfig);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public HashMap<String, FilterDto> getFilterDtos() {
        return filterDtos;
    }

    public List<String> getFilterNames() {
        return filterNames;
    }

    public HashMap<String, String> getPathsToFilters() {
        return pathsToFilters;
    }
}
