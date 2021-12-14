package ru.ramprox.server;

import ru.ramprox.server.dispatcher.DispatcherRequestFactory;
import ru.ramprox.server.handler.HandlerFactory;
import ru.ramprox.server.service.simpleserviceimpl.ServiceFactory;
import ru.ramprox.server.webserver.ServerFactory;

public class Factory {

    private ServiceFactory serviceFactory;
    private HandlerFactory handlerFactory;
    private DispatcherRequestFactory dispatcherRequestFactory;
    private ServerFactory serverFactory;

    public Factory() {
        this.serviceFactory = new ServiceFactory();
        this.handlerFactory = new HandlerFactory(serviceFactory);
        this.dispatcherRequestFactory =
                new DispatcherRequestFactory(serviceFactory, handlerFactory);
        this.serverFactory = new ServerFactory(dispatcherRequestFactory);
    }

    public ServiceFactory getServiceFactory() {
        return serviceFactory;
    }

    public HandlerFactory getHandlerFactory() {
        return handlerFactory;
    }

    public DispatcherRequestFactory getDispatcherRequestFactory() {
        return dispatcherRequestFactory;
    }

    public ServerFactory getServerFactory() {
        return serverFactory;
    }
}
