package ru.ramprox.server.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Класс, инкапсулирующий запрос
 */
public class Request {

    private RequestType type;

    private String requestedResource;

    private Map<String, String> header = new HashMap<>();

    public RequestType getType() {
        return type;
    }

    public void setType(RequestType type) {
        this.type = type;
    }

    public String getRequestedResource() {
        return requestedResource;
    }

    public void setRequestedResource(String requestedResource) {
        this.requestedResource = requestedResource;
    }

    public void addHeader(String headerName, String value) {
        header.put(headerName, value);
    }

    public String getHeader(String headerName) {
        return header.get(headerName);
    }

    public enum RequestType {
        GET
    }
}
