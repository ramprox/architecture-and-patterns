package ru.ramprox.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ramprox.server.factory.Utils;
import ru.ramprox.server.factory.environment.Environment;
import ru.ramprox.server.factory.contextconfig.ContextConfig;
import ru.ramprox.server.factory.context.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Класс, ответственный за инициализацию приложения
 */
public class Application {

    private static final String CONTEXT_PROPERTIES_FILE = "context.properties";
    private static final String CONTEXT_CONFIG_PROPERTY = "context.config";
    private static final String PATH_TO_BANNER = "/banner.txt";

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    /**
     * Запуск фабрики с реализацией Environment по умолчанию
     *
     * @param args - аргументы командной строки
     */
    public static Context run(String... args) {
        printBanner();
        return getContextConfig().getContext(args);
    }

    /**
     * Запуск фабрики с пользовательской реализацией Environment по умолчанию
     *
     * @param environment - пользовательская реализация Environment
     */
    public static Context run(Environment environment) {
        printBanner();
        return getContextConfig().getContext(environment);
    }

    /**
     * Печать баннера
     */
    private static void printBanner() {
        try (InputStream is = Application.class.getResourceAsStream(PATH_TO_BANNER);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            reader.lines().forEach(System.out::println);
        } catch (IOException ex) {
            logger.info("Can't load banner: {}", ex.getMessage());
        }
    }

    private static ContextConfig getContextConfig() {
        URL url = Application.class.getClassLoader().getResource(CONTEXT_PROPERTIES_FILE);
        String impl = Utils.readFromFile(url).get(CONTEXT_CONFIG_PROPERTY);
        return Utils.createObject(impl);
    }
}