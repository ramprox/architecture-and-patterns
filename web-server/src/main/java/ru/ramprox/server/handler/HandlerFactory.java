package ru.ramprox.server.handler;

import ru.ramprox.server.service.interfaces.ContentTypeResolver;
import ru.ramprox.server.service.interfaces.ResourceReader;
import ru.ramprox.server.service.interfaces.ResourceResolver;
import ru.ramprox.server.service.interfaces.SessionService;
import ru.ramprox.server.service.simpleserviceimpl.ServiceFactory;

import java.util.HashMap;
import java.util.Map;

public class HandlerFactory {

    private final ServiceFactory serviceFactory;
    private final Map<Class<?>, Object> objects;

    public HandlerFactory(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
        objects = initialize();
    }

    protected Map<Class<?>, Object> initialize() {
        Map<Class<?>, Object> result = new HashMap<>();
        RequestHandler h1 = new LastHandler(serviceFactory.get(ResourceResolver.class),
                serviceFactory.get(ContentTypeResolver.class),
                serviceFactory.get(ResourceReader.class));
        SessionService sessionService = serviceFactory.get(SessionService.class);
        RequestHandler h2 = new AuthenticationHandler(sessionService, h1);
        result.put(RequestHandler.class, new SessionHandler(sessionService, h2));

        ExceptionHandler exceptionHandler = new ExceptionHandlerImpl(serviceFactory.get(ResourceResolver.class),
                serviceFactory.get(ContentTypeResolver.class),
                serviceFactory.get(ResourceReader.class));
        result.put(ExceptionHandler.class, exceptionHandler);
        return result;
    }

    public <T> T get(Class<T> cl) {
        return cl.cast(objects.get(cl));
    }
}
