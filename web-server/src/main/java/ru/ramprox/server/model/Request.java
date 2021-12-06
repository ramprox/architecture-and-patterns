package ru.ramprox.server.model;

/**
 * Класс, инкапсулирующий запрос
 */
public class Request {
    private RequestType type;
    private String requestedResource;
    private String referer;

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

    public String getReferer() {
        return referer;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

    public enum RequestType {
        GET
    }
}
