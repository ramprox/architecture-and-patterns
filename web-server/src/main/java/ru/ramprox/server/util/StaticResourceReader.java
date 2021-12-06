package ru.ramprox.server.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Класс для чтения статического контента
 */
public class StaticResourceReader {

    private static final String CLASS_PATH = "classpath:";

    /**
     * Читает статический контент
     * @param path - путь к файлу
     * @return объект типа String, с содержимым файла по указанному пути path
     * @throws IOException - ошибки при чтении
     */
    public String read(String path) throws IOException {
        if (path.startsWith(CLASS_PATH)) {
            return readFromClasspath(path);
        }
        return readFromExternalResource(path);
    }

    /**
     * Чтение из classpath
     * @param path - путь
     * @return объект типа String, с содержимым файла по указанному пути path
     * @throws IOException - ошибки при чтении
     */
    private String readFromClasspath(String path) throws IOException {
        if(path.endsWith("/")) {
            throw new FileNotFoundException(path);
        }
        StringBuilder builder = new StringBuilder();
        InputStream stream = this.getClass().getResourceAsStream(path.substring(CLASS_PATH.length()));
        if (stream == null) {
            throw new FileNotFoundException(path);
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line).append("\n");
        }
        return builder.toString();
    }

    /**
     * Чтение из внешнего ресурса
     * @param path - путь
     * @return объект типа String, с содержимым файла по указанному пути path
     * @throws IOException - ошибки при чтении
     */
    private String readFromExternalResource(String path) throws IOException {
        StringBuilder builder = new StringBuilder();
        Path pathToResourse = Paths.get(path);
        if (!Files.exists(pathToResourse) || Files.isDirectory(pathToResourse)) {
            throw new FileNotFoundException(path);
        }
        Files.readAllLines(pathToResourse).forEach(line -> builder.append(line).append("\n"));
        return builder.toString();
    }
}
