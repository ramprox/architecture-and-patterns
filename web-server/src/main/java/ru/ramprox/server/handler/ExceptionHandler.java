package ru.ramprox.server.handler;

import ru.ramprox.server.model.HttpRequest;
import ru.ramprox.server.model.HttpResponse;

public interface ExceptionHandler {
    HttpResponse handle(HttpRequest request, Exception ex);
}
