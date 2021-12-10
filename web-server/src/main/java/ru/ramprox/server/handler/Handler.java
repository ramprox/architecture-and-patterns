package ru.ramprox.server.handler;

import ru.ramprox.server.model.Request;
import ru.ramprox.server.model.Response;

public interface Handler {
    void handleRequest(Request request, Response response) throws Exception;
}
