package ru.ramprox.server.service.simpleserviceimpl;

import ru.ramprox.server.model.Cookie;
import ru.ramprox.server.model.HttpResponse;
import ru.ramprox.server.model.ResponseHeaderName;
import ru.ramprox.server.service.interfaces.ResponseConverter;

import java.util.Set;

/**
 * Класс, формирующий ответ клиенту
 */
class ResponseHeaderConverter implements ResponseConverter {

    private static final String HTTP_1_1 = "HTTP/1.1";

    @Override
    public String convert(HttpResponse response) {
        StringBuilder builder = new StringBuilder();
        builder
                .append(HTTP_1_1)
                .append(" ")
                .append(response.getStatus())
                .append("\n");
        response.getHeaders()
                .forEach((k, v) -> builder
                        .append(k)
                        .append(": ")
                        .append(v)
                        .append("\n"));
        Set<Cookie> cookies = response.getCookies();
        if (cookies.size() > 0) {
            cookies.forEach(cookie -> builder
                    .append(ResponseHeaderName.SET_COOKIE)
                    .append(": ")
                    .append(cookie.toString())
                    .append("\n"));
        }
        builder.append("\n");
        return builder.toString();
    }
}