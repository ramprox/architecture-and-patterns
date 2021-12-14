package ru.ramprox.server.handler;

import ru.ramprox.server.model.*;
import ru.ramprox.server.service.interfaces.SessionService;

import java.util.Optional;
import java.util.UUID;

/**
 * Класс, отвечающий за обработку запросов с сессией
 */
class SessionHandler implements RequestHandler {

    private final SessionService sessionService;
    private final RequestHandler nextHandler;

    SessionHandler(SessionService sessionService, RequestHandler nextHandler) {
        this.sessionService = sessionService;
        this.nextHandler = nextHandler;
    }

    @Override
    public void handle(HttpRequest request, HttpResponse.Builder responseBuilder) throws Exception {
        Optional<Cookie> jSessionIdCookie = request.getCookies()
                .stream()
                .filter(value -> value.getName().equals(Cookie.JSESSION_ID))
                .findFirst();

        if (!jSessionIdCookie.isPresent()) {
            addCookieInResponse(responseBuilder);
        } else {
            Cookie cookie = jSessionIdCookie.get();
            UUID uuid = UUID.fromString(cookie.getValue());
            Session session = sessionService.getSession(uuid);
            if (session == null) {
                addCookieInResponse(responseBuilder);
                setZeroCookie(responseBuilder, cookie);
            }
        }
        nextHandler.handle(request, responseBuilder);
    }

    private void addCookieInResponse(HttpResponse.Builder responseBuilder) {
        UUID uuid = UUID.randomUUID();
        Session session = new Session();
        Cookie cookie = new Cookie.Builder(Cookie.JSESSION_ID, uuid.toString())
                .withDomain("localhost")
                .withPath("/")
                .withExpires(session.getExpireDate())
                .withHttpOnly()
                .build();
        sessionService.add(uuid, session);
        responseBuilder.withCookie(cookie);
    }

    private void setZeroCookie(HttpResponse.Builder builder, Cookie cookie) {
        Cookie zeroCookie = new Cookie.Builder(cookie.getName(), cookie.getValue())
                .withMaxAge(0)
                .build();
        builder.withCookie(zeroCookie);
    }
}
