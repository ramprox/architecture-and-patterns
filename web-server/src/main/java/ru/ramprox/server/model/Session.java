package ru.ramprox.server.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Session implements Serializable {
    private LocalDateTime creationTime;
    private LocalDateTime expireDate;
    private Map<String, Object> attributes;

    public Session() {
        creationTime = LocalDateTime.now();
        expireDate = creationTime.plusMinutes(3);
        attributes = new HashMap<>();
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public LocalDateTime getExpireDate() {
        return expireDate;
    }

    public void addAttribute(String attribute, Object value) {
        attributes.put(attribute, value);
    }

    public Object getAttribute(String attribute) {
        return attributes.get(attribute);
    }

    public Object deleteAttribute(String attribute) {
        return attributes.remove(attribute);
    }
}
