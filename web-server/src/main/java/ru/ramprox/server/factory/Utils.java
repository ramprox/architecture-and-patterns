package ru.ramprox.server.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Utils {

    private static final Logger logger = LoggerFactory.getLogger(Utils.class);

    public static Map<String, String> readFromFile(URL url) {
        Map<String, String> result = new HashMap<>();
        try(BufferedReader reader = new BufferedReader(new InputStreamReader((InputStream) url.getContent()))) {
            reader.lines().forEach(line -> {
                String[] parsedLine = parseLine(line);
                result.put(parsedLine[0], parsedLine[1]);
            });
        } catch (FileNotFoundException ex) {
            logger.error("File {} not found : {}", url, ex.getMessage());
        } catch (IOException ex) {
            logger.error("Error read file {}: {}", url, ex.getMessage());
        }
        return result;
    }

    private static String[] parseLine(String line) {
        String[] args = line.split("=");
        String[] result = new String[2];
        result[0] = args[0].trim();
        result[1] = args[1].trim();
        return result;
    }

    public static <T> T createObject(String impl) {
        try {
            Class<?> envClass = Class.forName(impl);
            return (T) createObject(envClass);
        } catch (ClassNotFoundException e) {
            logger.info("Can't create object for class {}: {}", impl, e.getMessage());
            throw new RuntimeException(e);
        }
    }

    static <T> T createObject(Class<T> aClass) {
        try {
            Constructor<T> constructor = aClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (NoSuchMethodException |
                InvocationTargetException |
                InstantiationException |
                IllegalAccessException e) {
            logger.info("Can't create object for class {}: {}", aClass, e.getMessage());
            throw new RuntimeException(e);
        }
    }
}