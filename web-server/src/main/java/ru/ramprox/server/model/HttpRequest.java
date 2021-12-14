package ru.ramprox.server.model;

import java.util.*;

/**
 * Класс, инкапсулирующий запрос
 */
public class HttpRequest {

    private final RequestType type;

    private final String resource;

    private final Set<Cookie> cookies = new HashSet<>();

    private final Map<String, String> header = new HashMap<>();

    private String body;

    private HttpRequest(RequestType type, String resource) {
        this.type = type;
        this.resource = resource;
    }

    public RequestType getType() {
        return type;
    }

    public String getResource() {
        return resource;
    }

    private void addHeader(String name, String value) {
        header.put(name, value);
    }

    public String getHeader(String headerName) {
        return header.get(headerName);
    }

    private void addCookie(Cookie cookie) {
        cookies.add(cookie);
    }

    public Set<Cookie> getCookies() {
        return Collections.unmodifiableSet(cookies);
    }

    public String getBody() {
        return body;
    }

    private void setBody(String body) {
        this.body = body;
    }

    public enum RequestType {
        GET, POST
    }

    public static class Builder {
        private HttpRequest httpRequest;

        public Builder(HttpRequest.RequestType type, String requestedResource) {
            this.httpRequest = new HttpRequest(type, requestedResource);
        }

        public Builder withHeader(String name, String value) {
            this.httpRequest.addHeader(name, value);
            return this;
        }

        public Builder withCookie(Cookie cookie) {
            this.httpRequest.addCookie(cookie);
            return this;
        }

        public Builder withBody(String body) {
            this.httpRequest.setBody(body);
            return this;
        }

        public HttpRequest build() {
            if(httpRequest.getType() == null) {
                throw new IllegalStateException("Request without type");
            }
            String requestedResource = httpRequest.getResource();
            if(requestedResource == null || requestedResource.isEmpty()) {
                throw new IllegalStateException("Request without resource");
            }
            return httpRequest;
        }
    }
}
