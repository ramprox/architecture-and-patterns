package ru.ramprox.server.service;

import ru.ramprox.server.config.PropertyName;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Класс для чтения статического контента
 */
public class StaticResourceReader {

    /**
     * Читает текстовый статический контент
     * @param path - путь к файлу
     * @return объект типа String, с содержимым файла по указанному пути path
     * @throws IOException - ошибки при чтении
     */
    public String read(String path) throws IOException {
        if (path.startsWith(PropertyName.CLASSPATH_PREFIX)) {
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
        try {
            String subStr = path.substring(PropertyName.CLASSPATH_PREFIX.length());
            URI uri = this.getClass().getResource(subStr).toURI();
            Path pathToResource;
            System.out.println(uri.toString());
            if(uri.toString().startsWith("jar:file")) {
                Map<String, String> env = new HashMap<>();
                String[] array = uri.toString().split("!");
                FileSystem fs = FileSystems.newFileSystem(URI.create(array[0]), env);
                pathToResource = fs.getPath(array[1]);
                List<String> lines = Files.readAllLines(pathToResource, Charset.defaultCharset());
                lines.forEach(line -> builder.append(line).append("\n"));
                fs.close();
            } else {
                pathToResource = Paths.get(uri);
                List<String> lines = Files.readAllLines(pathToResource, Charset.defaultCharset());
                lines.forEach(line -> builder.append(line).append("\n"));
            }
        } catch (URISyntaxException ex) {
            throw new FileNotFoundException(ex.getMessage());
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

    /**
     * Чтение статического ресурса в виде байтов (Например, если запрашивается изображение)
     * @param path
     * @return
     * @throws IOException
     */
    public byte[] readBytes(String path) throws IOException {
        if (path.startsWith(PropertyName.CLASSPATH_PREFIX)) {
            return readBytesFromClasspath(path);
        }
        return readBytesFromExternalResource(path);
    }

    /**
     * Чтение в виде байтов из classpath
     * @param path
     * @return
     * @throws IOException
     */
    private byte[] readBytesFromClasspath(String path) throws IOException {
        if(path.endsWith("/")) {
            throw new FileNotFoundException(path);
        }
        byte[] bytes;
        try {
            String subStr = path.substring(PropertyName.CLASSPATH_PREFIX.length());
            URI uri = this.getClass().getResource(subStr).toURI();
            if(uri.toString().startsWith("jar:file")) {
                final Map<String, String> env = new HashMap<>();
                final String[] array = uri.toString().split("!");
                final FileSystem fs = FileSystems.newFileSystem(URI.create(array[0]), env);
                final Path pathToResource = fs.getPath(array[1]);
                bytes = Files.readAllBytes(pathToResource);
                fs.close();
            } else {
                Path pathToResource = Paths.get(this.getClass().getResource(subStr).toURI());
                bytes = Files.readAllBytes(pathToResource);
            }
        } catch (URISyntaxException ex) {
            throw new FileNotFoundException(ex.getMessage());
        }
        return bytes;
    }

    private byte[] readBytesFromExternalResource(String path) throws IOException {
        Path pathToResourse = Paths.get(path);
        if (!Files.exists(pathToResourse) || Files.isDirectory(pathToResourse)) {
            throw new FileNotFoundException(path);
        }
        return Files.readAllBytes(pathToResourse);
    }
}
