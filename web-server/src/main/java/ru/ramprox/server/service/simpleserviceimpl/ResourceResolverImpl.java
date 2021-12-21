package ru.ramprox.server.service.simpleserviceimpl;

import ru.ramprox.server.annotation.Component;
import ru.ramprox.server.annotation.Value;
import ru.ramprox.server.config.PropertyName;
import ru.ramprox.server.service.interfaces.ResourceResolver;

/**
 * Класс, разрешающий путь к ресурсу
 */
@Component
class ResourceResolverImpl implements ResourceResolver {

    /**
     * Разрешает путь к ресурсу
     *
     * @param resource - запрошенный ресурс
     * @return разрешенный путь к ресурсу
     */
    @Value(name = PropertyName.PATH_TO_STATIC)
    private String pathToStatic;

    @Override
    public String resolve(String resource) {
        return pathToStatic + resource;
    }
}
