package ru.ramprox.server.service;

import ru.ramprox.server.model.Response;
import ru.ramprox.server.model.ResponseHeaderName;

/**
 * Класс, формирующий ответ клиенту
 */
public class BuilderResponseHeaderToString {

    public String build(Response response) {
        StringBuilder builder = new StringBuilder();
        builder.append("HTTP/1.1 ").append(response.getHeader(ResponseHeaderName.Status)).append("\n");
        response.getHeaders()
                .forEach((k, v) -> builder.append(k).append(": ").append(v).append("\n"));
        builder.append("\n");
        return builder.toString();
    }
}