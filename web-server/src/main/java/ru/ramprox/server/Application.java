package ru.ramprox.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ramprox.server.config.Environment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Класс, ответственный за инициализацию приложения
 */
public class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    /**
     * Запуск приложения
     * @param args - аргументы командной строки
     */
    public static void run(String[] args) {
        printBanner();
        Environment.loadSettings(args);
        new WebServer().start();
    }

    /**
     * Печать баннера
     */
    private static void printBanner() {
        try (InputStream is = Application.class.getResourceAsStream("/banner.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            reader.lines().forEach(System.out::println);
        } catch (IOException ex) {
            logger.error("Can't load banner: {}", ex.getMessage());
        }
    }
}
