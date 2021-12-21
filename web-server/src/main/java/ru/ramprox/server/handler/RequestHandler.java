package ru.ramprox.server.handler;

import ru.ramprox.server.model.HttpRequest;
import ru.ramprox.server.model.HttpResponse;

public interface RequestHandler {
    HttpResponse handle(HttpRequest request) throws Exception;
}
