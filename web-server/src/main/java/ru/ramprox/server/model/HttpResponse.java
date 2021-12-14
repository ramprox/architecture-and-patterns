package ru.ramprox.server.model;

import java.util.*;

/**
 * Класс, инкапсулирующий ответ
 */
public class HttpResponse {
    private Object body;
    private HttpResponseStatus status;
    private final Set<Cookie> cookies = new HashSet<>();
    private final Map<Object, Object> headers = new HashMap<>();

    private HttpResponse() {
    }

    public Object getBody() {
        return body;
    }

    private void setBody(Object body) {
        this.body = body;
    }

    void setStatus(HttpResponseStatus status) {
        this.status = status;
    }

    public HttpResponseStatus getStatus() {
        return status;
    }

    private void setHeader(Object key, Object value) {
        headers.put(key, value);
    }

    public Map<Object, Object> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }

    private void addCookie(Cookie cookie) {
        cookies.add(cookie);
    }

    public Set<Cookie> getCookies() {
        return Collections.unmodifiableSet(cookies);
    }

    public static class Builder {
        private final HttpResponse httpResponse;

        public Builder() {
            httpResponse = new HttpResponse();
        }

        public Builder withStatus(HttpResponseStatus status) {
            httpResponse.setStatus(status);
            return this;
        }

        public Builder withHeader(Object key, Object value) {
            httpResponse.setHeader(key, value);
            return this;
        }

        public Builder withBody(Object body) {
            httpResponse.setBody(body);
            return this;
        }

        public Builder withCookie(Cookie cookie) {
            httpResponse.addCookie(cookie);
            return this;
        }

        public HttpResponse build() {
            if(httpResponse.getStatus() == null) {
                throw new IllegalStateException("Response without status");
            }
            return httpResponse;
        }
    }
}