package ru.ramprox.server.service.interfaces;

import ru.ramprox.server.model.HttpRequest;

import java.util.Queue;

public interface RequestParser {
    HttpRequest parseRequest(Queue<String> request);
}
