package ru.ramprox.server.dispatcher;

import java.net.Socket;

public interface DispatcherRequest {
    void dispatchRequest(Socket socket);
}
