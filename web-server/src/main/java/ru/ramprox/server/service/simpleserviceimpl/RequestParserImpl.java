package ru.ramprox.server.service.simpleserviceimpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ramprox.server.model.*;
import ru.ramprox.server.service.interfaces.RequestParser;

import java.util.Queue;
import java.util.stream.Collectors;

/**
 * Класс, который парсит запрос
 */
class RequestParserImpl implements RequestParser {

    private static final Logger logger = LoggerFactory.getLogger(RequestParserImpl.class);

    /**
     * Парсит из объекта типа String объект типа HttpRequest
     *
     * @param request запрос в виде строки
     * @return объект типа HttpRequest, инкапсулирующий запрос
     */
    @Override
    public HttpRequest parseRequest(Queue<String> request) {
        try {
            HttpRequest.Builder builder = parseFirstLine(request);
            parseHeaders(builder, request);
            parseBody(builder, request);
            return builder.build();
        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException ex) {
            throw new IllegalStateException("Unknown request", ex);
        }
    }

    /**
     * Парсинг первой строки
     *
     * @param requestLines
     */
    private HttpRequest.Builder parseFirstLine(Queue<String> requestLines) {
        String[] parts = requestLines.poll().split(" ");
        String requestTypeString = parts[0];
        String requestedResource = parts[1];
        HttpRequest.RequestType requestType = HttpRequest.RequestType.valueOf(requestTypeString);
        return new HttpRequest.Builder(requestType, requestedResource);
    }

    /**
     * Парсинг заголовков
     *
     * @param lines
     */
    private void parseHeaders(HttpRequest.Builder builder, Queue<String> lines) {
        do {
            String line = lines.poll();
            if (line == null || line.equals("")) {
                break;
            }
            String[] header = line.split(": ");
            if(RequestHeaderName.COOKIE.equals(header[0])) {
                String[] cookies = header[1].split("; ");
                for(String cookieString : cookies) {
                    String[] cookieStringArr = cookieString.split("=");
                    Cookie cookie = new Cookie.Builder(cookieStringArr[0], cookieStringArr[1])
                            .build();
                    builder.withCookie(cookie);
                }
            } else {
                builder.withHeader(header[0], header[1]);
            }
        } while (true);
    }

    /**
     * Парсинг тела запроса
     *
     * @param lines
     */
    private void parseBody(HttpRequest.Builder builder, Queue<String> lines) {
        String body = lines.stream()
                .peek(logger::info)
                .filter(line -> !line.equals(""))
                .collect(Collectors.joining());
        builder.withBody(body);
        lines.clear();
    }
}
