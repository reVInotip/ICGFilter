package Factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.FiltersFactory;
import model.filters.FiltersModel;
import objectsFromJson.ConfigsPath;
import objectsFromJson.ConfigPath;
import objectsFromJson.parsedConfigurationObjects.ModelConfig;
import org.example.App;

import java.io.InputStream;
import java.util.HashMap;


public class MainFactory {
    static private final String RESOURCE_FILE_NAME  = "/filtersPathToConfig.json";
    //тут происходит получение HashMap со всеми конфигами преобразованными в объект
    static private final HashMap<String, ModelConfig> allInfAtConfig = new HashMap<>();

    public static void initFactory(){
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
                ModelConfig modelConfig = mapper.readValue(inputStream, ModelConfig.class);

                allInfAtConfig.put(configPath.getName(), modelConfig);
                System.out.println(modelConfig);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        FiltersFactory.initFactory(allInfAtConfig);

        //DialogFactory.initFactory(allInfAtConfig);
    }

    public static HashMap<String, FiltersModel> createModels() {
        return FiltersFactory.createFilters();
//        for (Map.Entry<String, ModelConfig> modelInfo: allInfAtConfig.entrySet()) {
//            FiltersFactory.createFilterModel(modelInfo.getKey(), modelInfo.getValue());
//        }
    }
}
