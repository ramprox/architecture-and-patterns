package ru.ramprox.server.handler;

import ru.ramprox.server.model.HttpRequest;
import ru.ramprox.server.model.HttpResponse;

public interface ExceptionHandler {
    void handle(HttpRequest request, HttpResponse.Builder responseBuilder,
                Exception ex) throws Exception;
}
