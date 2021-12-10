package ru.ramprox.server.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Класс, инкапсулирующий ответ
 */
public class Response {
    private Object body;
    private Map<Object, Object> headers = new HashMap<>();

    public Object getContent() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    public void setHeader(Object key, Object value) {
        headers.put(key, value);
    }

    public Object getHeader(Object key) {
        return headers.get(key);
    }

    public Map<Object, Object> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }

    public enum Status {
        OK(200), NOT_FOUND(404), REDIRECT(302);
        private int code;

        Status(int code) {
            this.code = code;
        }

        @Override
        public String toString() {
            return code + " " + super.toString();
        }
    }
}