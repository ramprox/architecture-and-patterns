package ru.ramprox.server.handler;

import ru.ramprox.server.model.*;
import ru.ramprox.server.service.SessionService;

import java.util.UUID;

/**
 * Класс, отвечающий за обработку запросов с сессией
 */
public class SessionHandler implements Handler {

    private final SessionService sessionService;
    private final Handler nextHandler;

    public SessionHandler(SessionService sessionService, Handler nextHandler) {
        this.sessionService = sessionService;
        this.nextHandler = nextHandler;
    }

    public void handleRequest(Request request, Response response) throws Exception {
        String cookie = request.getHeader(RequestHeaderName.COOKIE);
        if (cookie == null) {
            addCookieInResponse(response);
        } else {
            UUID uuid = UUID.fromString(cookie.split("=")[1]);
            Session session = sessionService.getSession(uuid);
            if (session == null) {
                addCookieInResponse(response);
                request.deleteHeader(RequestHeaderName.COOKIE);
            }
        }
        nextHandler.handleRequest(request, response);
    }

    private void addCookieInResponse(Response response) {
        UUID uuid = UUID.randomUUID();
        Session session = new Session();
        Cookie cookie = new Cookie("JSESSIONID");
        cookie.setValue(uuid.toString());
        cookie.setPath("/");
        cookie.setDomain("localhost");
        cookie.setHttpOnly(true);
        cookie.setExpires(session.getExpireDate());
        sessionService.add(uuid, session);
        response.setHeader(ResponseHeaderName.SET_COOKIE, cookie);
    }
}
