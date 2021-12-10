package ru.ramprox.server.service;

import ru.ramprox.server.config.Environment;
import ru.ramprox.server.config.PropertyName;

/**
 * Класс, разрешающий путь к ресурсу
 */
public class ResourceResolver {

    /**
     * Разрешает путь к ресурсу
     *
     * @param resource - запрошенный ресурс
     * @return разрешенный путь к ресурсу
     */
    public String resolve(String resource) {
        return Environment.getProperty(PropertyName.PATH_TO_STATIC) + resource;
    }
}
