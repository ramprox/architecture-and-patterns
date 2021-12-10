package ru.ramprox.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ramprox.server.model.Request;

import java.util.Queue;
import java.util.stream.Collectors;

/**
 * Класс, который парсит запрос
 */
public class RequestParser {

    private static final Logger logger = LoggerFactory.getLogger(RequestParser.class);

    /**
     * Парсит из объекта типа String объект типа Request
     *
     * @param request запрос в виде строки
     * @return объект типа Request, инкапсулирующий запрос
     */
    public Request parseRequest(Queue<String> request) {
        try {
            Request result = new Request();
            parseFirstLine(result, request);
            parseHeaders(result, request);
            parseBody(result, request);
            return result;
        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException ex) {
            throw new IllegalStateException("Unknown request", ex);
        }
    }

    /**
     * Парсинг первой строки
     *
     * @param request
     * @param requestLines
     */
    private void parseFirstLine(Request request, Queue<String> requestLines) {
        String[] parts = requestLines.poll().split(" ");
        String requestTypeString = parts[0];
        String requestedResource = parts[1];
        Request.RequestType requestType = Request.RequestType.valueOf(requestTypeString);
        request.setType(requestType);
        request.setRequestedResource(requestedResource);
    }

    /**
     * Парсинг заголовков
     *
     * @param request
     * @param lines
     */
    private void parseHeaders(Request request, Queue<String> lines) {
        do {
            String line = lines.poll();
            if (line == null || line.equals("")) {
                break;
            }
            String[] header = line.split(": ");
            request.addHeader(header[0], header[1]);
        } while (true);
    }

    /**
     * Парсинг тела запроса
     *
     * @param request
     * @param lines
     */
    private void parseBody(Request request, Queue<String> lines) {
        String body = lines.stream()
                .peek(logger::info)
                .filter(line -> !line.equals(""))
                .collect(Collectors.joining());
        request.setBody(body);
        lines.clear();
    }
}
