package Factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.tools.javac.Main;
import model.filters.IFiltersModel;
import objectsFromJson.ConfigsPath;
import objectsFromJson.ConfigPath;
import objectsFromJson.parsedConfigurationObjects.ModelConfig;
import org.example.App;

import java.io.InputStream;
import java.util.HashMap;


public class MainFactory {
    static private final String RESOURCE_FILE_NAME  = "/filtersPathToConfig.json";
    static HashMap<String, IFiltersModel> models = new HashMap<String, IFiltersModel>();
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

        //тут происходит получение HashMap со всеми конфигами преобразованными в объект
        HashMap<String, ModelConfig> allInfAtConfig = new HashMap<String, ModelConfig>();
        for (ConfigPath configPath : filtersConfigPath.getToolPath()) {
            try {
                InputStream inputStream = App.class.getResourceAsStream(configPath.getPath());

                if (inputStream == null) {
                    throw new RuntimeException("Файл не найден: " + configPath.getPath());
                }

                // Парсим JSON в объект
                ModelConfig modelConfig = mapper.readValue(inputStream, ModelConfig.class);

                allInfAtConfig.put(configPath.getName(), modelConfig);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        //ModelFactory.initFactory(allInfAtConfig);

        //DialogFactory.initFactory(allInfAtConfig);
    }
}
