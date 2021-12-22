package ru.ramprox.server.dispatcher;

import ru.ramprox.server.handler.ExceptionHandler;
import ru.ramprox.server.handler.HandlerFactory;
import ru.ramprox.server.handler.RequestHandler;
import ru.ramprox.server.service.interfaces.RequestParser;
import ru.ramprox.server.service.interfaces.ResponseConverter;
import ru.ramprox.server.service.simpleserviceimpl.ServiceFactory;

public class DispatcherRequestFactory {
    private final HandlerFactory handlerFactory;
    private final ServiceFactory serviceFactory;
    private final DispatcherRequest dispatcherRequest;

    public DispatcherRequestFactory(ServiceFactory serviceFactory, HandlerFactory handlerFactory) {
        this.serviceFactory = serviceFactory;
        this.handlerFactory = handlerFactory;
        this.dispatcherRequest = initialize();
    }

    protected DispatcherRequest initialize() {
        return new DispatcherRequestImpl(
                serviceFactory.get(RequestParser.class),
                serviceFactory.get(ResponseConverter.class),
                handlerFactory.get(ExceptionHandler.class),
                handlerFactory.get(RequestHandler.class),
                serviceFactory);
    }

    public DispatcherRequest getDispatcherRequest() {
        return dispatcherRequest;
    }
}
