package ru.ramprox.server.webserver;

import ru.ramprox.server.dispatcher.DispatcherRequestFactory;

public class ServerFactory {

    private final DispatcherRequestFactory dispatcherRequestFactory;

    public ServerFactory(DispatcherRequestFactory dispatcherRequestFactory) {
        this.dispatcherRequestFactory = dispatcherRequestFactory;
    }

    public Server getServer() {
        return new WebServer(dispatcherRequestFactory.getDispatcherRequest());
    }
}
