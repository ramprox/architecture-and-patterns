package ru.ramprox.server.service.simpleserviceimpl;

import ru.ramprox.server.config.PropertyName;
import ru.ramprox.server.service.interfaces.ResourceReader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Класс для чтения статического контента
 */
class StaticResourceReader implements ResourceReader {


    /**
     * Читает текстовый статический контент
     * @param path - путь к файлу
     * @return объект типа String, с содержимым файла по указанному пути path
     * @throws IOException - ошибки при чтении
     */
    public String read(String path) throws IOException {
        byte[] bytes = readBytes(path);
        return new String(bytes, Charset.defaultCharset());
    }

    /**
     * Чтение статического ресурса в виде байтов (Например, если запрашивается изображение)
     * @param path
     * @return
     * @throws IOException
     */
    public byte[] readBytes(String path) throws IOException {
        if(path.endsWith("/")) {
            throw new FileNotFoundException(path);
        }
        if (path.startsWith(PropertyName.CLASSPATH_PREFIX)) {
            return readBytesFromClasspath(path);
        }
        return readBytesFromExternalResource(path);
    }

    /**
     * Чтение в байтов из classpath
     * @param path - путь к ресурсу
     * @return - прочитанные байты
     * @throws IOException
     */
    private byte[] readBytesFromClasspath(String path) throws IOException {
        String subStr = path.substring(PropertyName.CLASSPATH_PREFIX.length());
        InputStream is = this.getClass().getResourceAsStream(subStr);
        if(is == null) {
            throw new FileNotFoundException(path);
        }
        byte[] bytes = new byte[is.available()];
        is.read(bytes);
        return bytes;
    }

    /**
     * Чтение байтов из внешнего ресурса
     * @param path - путь к ресурсу
     * @return - прочитанные байты
     * @throws IOException
     */
    private byte[] readBytesFromExternalResource(String path) throws IOException {
        Path pathToResource = Paths.get(path);
        if (!Files.exists(pathToResource) || Files.isDirectory(pathToResource)) {
            throw new FileNotFoundException(path);
        }
        return Files.readAllBytes(pathToResource);
    }
}
