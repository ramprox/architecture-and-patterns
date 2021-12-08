package ru.ramprox.server.util;

import ru.ramprox.server.model.Request;

import java.util.Queue;

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
    public Request parseRequest(Queue<String> request) {
        try {
            Request result = new Request();
            parseFirstLine(result, request);
            parseHeaders(result, request);
            return result;
        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException ex) {
            throw new IllegalStateException("Unknown request", ex);
        }
    }

    private void parseFirstLine(Request request, Queue<String> requestLines) {
        String[] parts = requestLines.poll().split(" ");
        String requestTypeString = parts[0];
        String requestedResource = parts[1];
        Request.RequestType requestType = Request.RequestType.valueOf(requestTypeString);
        request.setType(requestType);
        request.setRequestedResource(requestedResource);
    }

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
}
