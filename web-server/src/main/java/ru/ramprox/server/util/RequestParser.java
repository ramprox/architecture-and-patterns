package ru.ramprox.server.util;

import ru.ramprox.server.model.Request;

import java.util.Arrays;
import java.util.List;

/**
 * Класс, который парсит запрос
 */
public class RequestParser {

    /**
     * Парсит из объекта типа String объект типа Request
     *
     * @param request запрос в виде строки
     * @return объект типа Request, инкапсулирующий запрос
     */
    public Request parseRequest(String request) {
        List<String> lines = List.of(request.split("\n"));
        if(lines.size() == 0) {
            throw new IllegalStateException("Unknown request");
        }
        Request result = new Request();
        parseFirstLine(result, lines.get(0));
        parseHeaders(result, lines.subList(1, lines.size()));
        return result;
    }

    private void parseFirstLine(Request request, String stringRequest) {
        String[] parts = stringRequest.split(" ");
        String requestType = parts[0];
        if ("GET".equals(requestType)) {
            request.setType(Request.RequestType.GET);
            request.setRequestedResource(parts[1]);
        } else {
            throw new IllegalStateException("Unknown request");
        }
    }

    private void parseHeaders(Request request, List<String> lines) {
        lines.forEach(s -> {
            if(s.startsWith("Referer: ")) {
                request.setReferer(s.substring("Referer: ".length()));
            }
        });
    }
}
