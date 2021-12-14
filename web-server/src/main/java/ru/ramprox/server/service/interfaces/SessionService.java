package ru.ramprox.server.service.interfaces;

import ru.ramprox.server.model.Session;

import java.util.UUID;

public interface SessionService {
    void add(UUID uuid, Session session);
    Session getSession(UUID uuid);
    void saveSessionsInStorage();
}
