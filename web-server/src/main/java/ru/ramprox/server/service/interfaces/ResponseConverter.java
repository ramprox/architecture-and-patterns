package ru.ramprox.server.service.interfaces;

import ru.ramprox.server.model.HttpResponse;

public interface ResponseConverter {
    String convert(HttpResponse response);
}
