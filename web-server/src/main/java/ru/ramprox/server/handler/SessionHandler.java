package ru.ramprox.server.handler;

import ru.ramprox.server.annotation.Component;
import ru.ramprox.server.annotation.Handler;
import ru.ramprox.server.annotation.Inject;
import ru.ramprox.server.model.*;
import ru.ramprox.server.service.interfaces.SessionService;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Класс, отвечающий за обработку запросов с сессией
 */
@Component(name = "sessionHandler")
@Handler(order = 1)
class SessionHandler implements RequestHandler {

    private final SessionService sessionService;
    private final RequestHandler nextHandler;

    @Inject
    SessionHandler(SessionService sessionService, RequestHandler nextHandler) {
        this.sessionService = sessionService;
        this.nextHandler = nextHandler;
    }

    @Override
    public HttpResponse handle(HttpRequest request) throws Exception {
        Optional<Cookie> jSessionIdCookie = getCookieByName(request.getCookies(), Cookie.JSESSION_ID);
        Cookie cookie;
        if (!jSessionIdCookie.isPresent()) {
            cookie = createSession();
            return redirectToPage(cookie, request.getResource());
        } else {
            cookie = jSessionIdCookie.get();
            UUID uuid = UUID.fromString(cookie.getValue());
            Session session = sessionService.getSession(uuid);
            if (session == null) {
                cookie = createSession();
                return redirectToPage(cookie, request.getResource());
            }
        }
        return nextHandler.handle(request);
    }

    private Optional<Cookie> getCookieByName(Set<Cookie> cookies, String name) {
        return cookies.stream()
                .filter(cookie -> cookie.getName().equals(name))
                .findFirst();
    }

    private Cookie createSession() {
        UUID uuid = UUID.randomUUID();
        Session session = new Session();
        Cookie cookie = new Cookie.Builder(Cookie.JSESSION_ID, uuid.toString())
                .withDomain("localhost")
                .withPath("/")
                .withExpires(session.getExpireDate())
                .withHttpOnly()
                .build();
        sessionService.add(uuid, session);
        return cookie;
    }

    private HttpResponse redirectToPage(Cookie cookie, String page) {
        return new HttpResponse.Builder()
                .withHeader(ResponseHeaderName.LOCATION, page)
                .withStatus(HttpResponseStatus.REDIRECT)
                .withCookie(cookie)
                .build();
    }
}
