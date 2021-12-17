package ru.ramprox.server;

import ru.ramprox.server.dispatcher.DispatcherRequestFactory;
import ru.ramprox.server.handler.HandlerFactory;
import ru.ramprox.server.service.simpleserviceimpl.ServiceFactory;
import ru.ramprox.server.webserver.ServerFactory;

class Factory {

    private ServiceFactory serviceFactory;
    private HandlerFactory handlerFactory;
    private DispatcherRequestFactory dispatcherRequestFactory;
    private ServerFactory serverFactory;

    private Factory() {
        this.serviceFactory = new ServiceFactory();
        this.handlerFactory = new HandlerFactory(serviceFactory);
        this.dispatcherRequestFactory =
                new DispatcherRequestFactory(serviceFactory, handlerFactory);
        this.serverFactory = new ServerFactory(dispatcherRequestFactory);
    }

    static Factory createFactory() {
        return new Factory();
    }

    ServiceFactory getServiceFactory() {
        return serviceFactory;
    }

    HandlerFactory getHandlerFactory() {
        return handlerFactory;
    }

    DispatcherRequestFactory getDispatcherRequestFactory() {
        return dispatcherRequestFactory;
    }

    ServerFactory getServerFactory() {
        return serverFactory;
    }
}
