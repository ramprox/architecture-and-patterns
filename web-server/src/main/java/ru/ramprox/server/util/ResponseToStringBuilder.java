package ru.ramprox.server.util;

import ru.ramprox.server.model.Response;

/**
 * Класс, формирующий ответ клиенту
 */
public class ResponseToStringBuilder {

    public String build(Response response) {
        StringBuilder builder = new StringBuilder();
        builder.append("HTTP/1.1 ").append(response.getStatus()).append("\n");
        builder.append("Content-Type: ")
                .append(response.getContentType())
                .append(";")
                .append("charset=utf-8\n\n");
        builder.append(response.getContent());
        return builder.toString();
    }
}
