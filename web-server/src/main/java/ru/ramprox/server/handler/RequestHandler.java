package ru.ramprox.server.handler;

import ru.ramprox.server.model.HttpRequest;
import ru.ramprox.server.model.HttpResponse;

public interface RequestHandler {
    void handle(HttpRequest request, HttpResponse.Builder response) throws Exception;
}
