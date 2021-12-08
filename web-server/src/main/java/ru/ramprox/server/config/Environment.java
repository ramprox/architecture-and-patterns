package ru.ramprox.server.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.ramprox.server.WebServer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Класс, инкапсулирующий свойства приложения.
 */
public class Environment {

    private static final int DEFAULT_PORT = 8080;
    private static final String DEFAULT_PATH_TO_STATIC = "classpath:/static";

    private static final Properties properties;

    private static final Logger logger = LogManager.getLogger(Environment.class);

    /**
     * Происходит чтение и установка свойств
     */
    static {
        properties = new Properties();
        setDefaultSettings();
        setUserProperties();
    }

    /**
     * Установка настроек по умолчанию
     */
    private static void setDefaultSettings() {
        properties.setProperty(PropertyName.PORT, Integer.toString(DEFAULT_PORT));
        properties.setProperty(PropertyName.PATH_TO_STATIC, DEFAULT_PATH_TO_STATIC);
    }

    /**
     * Происходит чтение свойств из файла "application.properties", расположенного в корне classpath.
     * Если этот файл присутствует, то определенные в нем свойства переопределяют настройки по умолчанию
     */
    private static void setUserProperties() {
        try {
            InputStream stream = WebServer.class.getResourceAsStream("/application.properties");
            if (stream != null) {
                properties.load(stream);
            }
        } catch (IOException ex) {
            logger.error("Error read application.properties: {}", ex.getMessage());
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}
