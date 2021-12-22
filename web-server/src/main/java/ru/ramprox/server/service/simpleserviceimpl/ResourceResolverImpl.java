package ru.ramprox.server.service.simpleserviceimpl;

import ru.ramprox.server.config.Environment;
import ru.ramprox.server.config.PropertyName;
import ru.ramprox.server.service.interfaces.ResourceResolver;

/**
 * Класс, разрешающий путь к ресурсу
 */
class ResourceResolverImpl implements ResourceResolver {

    /**
     * Разрешает путь к ресурсу
     *
     * @param resource - запрошенный ресурс
     * @return разрешенный путь к ресурсу
     */
    @Override
    public String resolve(String resource) {
        return Environment.getProperty(PropertyName.PATH_TO_STATIC) + resource;
    }
}
