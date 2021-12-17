package ru.ramprox.server.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Класс, инкапсулирующий свойства приложения.
 */
public class Environment {

    private static final Properties properties = new Properties();

    private static final Logger logger = LoggerFactory.getLogger(Environment.class);

    /**
     * Происходит чтение и установка свойств
     */
    public static void loadSettings(String[] commandLineArgs) {

        loadDefaultSettings();
        loadFromClassPath();
        loadFromCommandLineArgs(commandLineArgs);
    }

    /**
     * Установка настроек по умолчанию
     */
    private static void loadDefaultSettings() {
        properties.setProperty(PropertyName.PORT, Integer.toString(DefaultSettings.SERVER_PORT));
        properties.setProperty(PropertyName.PATH_TO_STATIC, DefaultSettings.PATH_TO_STATIC);
        properties.setProperty(PropertyName.PATH_TO_SESSIONS, DefaultSettings.PATH_TO_SESSIONS);
    }

    /**
     * Происходит чтение свойств из файла "application.properties", расположенного в корне classpath.
     * Если этот файл присутствует, то определенные в нем свойства переопределяют настройки по умолчанию
     */
    private static void loadFromClassPath() {
        try {
            InputStream stream = Environment.class.getResourceAsStream(
                    DefaultSettings.CONFIG_LOCATION.substring(PropertyName.CLASSPATH_PREFIX.length()));
            if (stream != null) {
                properties.load(stream);
            }
        } catch (IOException ex) {
            logger.error("Error read application.properties: {}", ex.getMessage());
        }
    }

    /**
     * Загрузка настроек из аргументов командной строки. Если среди аргументов командной строки
     * содержится --config.location=fileName, то сначала происходит загрузка из этого указанного местоположения fileName,
     * а потом загрузка остальных свйоств, указанных в аргументах
     *
     * @param commandLineArgs - аргументы командной строки
     */
    private static void loadFromCommandLineArgs(String[] commandLineArgs) {
        if (commandLineArgs.length == 0) {
            return;
        }
        Map<String, String> args;
        try {
            args = parseCommandLineArgs(commandLineArgs);
        } catch (Exception ex) {
            logger.error("Can't parse command line args: {}", ex.getMessage());
            return;
        }
        String configLocation = args.remove(PropertyName.CONFIG_LOCATION);
        if (configLocation != null) {
            loadFromUserDefinedResource(configLocation);
        }
        properties.putAll(args);
    }

    /**
     * Парсинг аргументов командной строки
     * @param commandLineArgs - аргументы командной строки
     * @return объект типа Map<String, String>
     */
    private static Map<String, String> parseCommandLineArgs(String[] commandLineArgs) {
        Map<String, String> args = new HashMap<>();
        Arrays.stream(commandLineArgs).forEach(line -> {
            line = line.substring("--".length());
            String[] keyValues = line.split("=");
            args.put(keyValues[0], keyValues[1]);
        });
        return args;
    }


    /**
     * Загрузка настроек из указанного файла
     * @param path - путь к файлу
     */
    private static void loadFromUserDefinedResource(String path) {
        try {
            InputStream is;
            if(path.startsWith(PropertyName.CLASSPATH_PREFIX)) {
                path = path.substring(PropertyName.CLASSPATH_PREFIX.length());
                is = Environment.class.getResourceAsStream(path);
            } else {
                is = new FileInputStream(Paths.get(path).toFile());
            }
            properties.load(is);
        } catch (FileNotFoundException ex) {
            logger.error("File {} not found : {}", path, ex.getMessage());
        } catch (IOException ex) {
            logger.error("Can't load file {}. Cause: {}", path, ex.getMessage());
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}