package ru.ramprox.server.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ramprox.server.config.Environment;
import ru.ramprox.server.config.PropertyName;
import ru.ramprox.server.model.*;
import ru.ramprox.server.service.interfaces.SessionService;

import java.util.*;

/**
 * Класс,ответственный за аутентификацию клиента
 */
class AuthenticationHandler implements RequestHandler {

    private final RequestHandler nextHandler;
    private final SessionService sessionService;

    private final Set<String> pagesRequiredAuthentication = new HashSet<>();
    private static final String LOGIN_PROCESSING_PAGE = "/login_processing";
    private static final String MAIN_PAGE = "/index.html";
    private static final String LOGIN_PAGE= "/login.html";

    private static final String USER_ATTRIBUTE = "User";

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationHandler.class);

    AuthenticationHandler(SessionService sessionService, RequestHandler nextHandler) {
        this.nextHandler = nextHandler;
        this.sessionService = sessionService;
        String[] authPages = Environment.getProperty(PropertyName.PAGES_REQUIRED_AUTH).split(";");
        Arrays.stream(authPages).forEach(page -> pagesRequiredAuthentication.add("/" + page));
    }

    /**
     * Обработка запроса
     *
     * @param request - Принятый запрос
     * @param responseBuilder - Builder ответа
     * @throws Exception - ошибка обработки
     */
    @Override
    public void handle(HttpRequest request, HttpResponse.Builder responseBuilder) throws Exception {
        try {
            if (request.getResource().equals(LOGIN_PROCESSING_PAGE)) {
                loginProcessing(request, responseBuilder);
                return;
            }
            if (pagesRequiredAuthentication.contains(request.getResource())) {
                handleRequestRequiredAuthentication(request, responseBuilder);
                return;
            }
            nextHandler.handle(request, responseBuilder);
        } catch (IllegalArgumentException ex) {
            logger.error("Can't parse cookie header from request {}", ex.getMessage());
        }
    }

    /**
     * Обработка аутентификации
     *
     * @param request - Запрос, содержащий credentials
     * @param responseBuilder - Builder ответа
     */
    private void loginProcessing(HttpRequest request, HttpResponse.Builder responseBuilder) {
        String body = request.getBody();
        Credentials credentials = convertToCredentials(body);
        User user = new User(credentials.getUsername(), true);
        Set<Cookie> cookies = request.getCookies();
        Optional<Cookie> optJSessionCookie = getCookieByName(cookies, Cookie.JSESSION_ID);
        if(optJSessionCookie.isPresent()) {
            UUID uuid = UUID.fromString(optJSessionCookie.get().getValue());
            Session session = sessionService.getSession(uuid);
            session.addAttribute(USER_ATTRIBUTE, user);
            Optional<Cookie> optPageCookie = getCookieByName(cookies, Cookie.REQUESTED_PAGE);
            if(optPageCookie.isPresent()) {
                Cookie requestedPageCookie = optPageCookie.get();
                setZeroCookie(responseBuilder, requestedPageCookie);
                redirectToPage(responseBuilder, requestedPageCookie.getValue());
            } else {
                redirectToPage(responseBuilder, MAIN_PAGE);
            }
        }
    }

    /**
     * Обработка запросов, требующих аутентификацию
     *
     * @param request - Запрос, требующий аутентификацию
     * @param responseBuilder - Builder ответа
     * @throws Exception - ошибка обработки
     */
    private void handleRequestRequiredAuthentication(HttpRequest request, HttpResponse.Builder responseBuilder) throws Exception {
        Set<Cookie> cookies = request.getCookies();
        Optional<Cookie> jSessionIdCookie = getCookieByName(cookies, Cookie.JSESSION_ID);
        if (!jSessionIdCookie.isPresent()) {
            Cookie redirectCookie =
                    new Cookie.Builder(Cookie.REQUESTED_PAGE, request.getResource())
                    .build();
            responseBuilder.withCookie(redirectCookie);
            redirectToPage(responseBuilder, LOGIN_PAGE);
            return;
        }
        UUID uuid = UUID.fromString(jSessionIdCookie.get().getValue());
        Session session = sessionService.getSession(uuid);
        if(session != null) {
            User user = (User) session.getAttribute(USER_ATTRIBUTE);
            if (user != null && user.isAuthenticated()) {
                handleAuthenticatedUser(request, responseBuilder, cookies);
            } else {
                redirectToLoginPage(request, responseBuilder);
            }
        } else {
            redirectToLoginPage(request, responseBuilder);
        }
    }

    private void handleAuthenticatedUser(HttpRequest request, HttpResponse.Builder responseBuilder, Set<Cookie> cookies) throws Exception {
        Optional<Cookie> requestedPageCookie = getCookieByName(cookies, Cookie.REQUESTED_PAGE);
        if(requestedPageCookie.isPresent()) {
            Cookie cookie = requestedPageCookie.get();
            setZeroCookie(responseBuilder, cookie);
            redirectToPage(responseBuilder, cookie.getValue());
        } else {
            nextHandler.handle(request, responseBuilder);
        }
    }

    private void redirectToLoginPage(HttpRequest request, HttpResponse.Builder builder) {
        Cookie redirectCookie =
                new Cookie.Builder(Cookie.REQUESTED_PAGE, request.getResource())
                        .build();
        builder.withCookie(redirectCookie);
        redirectToPage(builder, LOGIN_PAGE);
    }

    private void redirectToPage(HttpResponse.Builder responseBuilder, String page) {
        responseBuilder
                .withHeader(ResponseHeaderName.LOCATION, page)
                .withStatus(HttpResponseStatus.REDIRECT);
    }

    private Credentials convertToCredentials(String body) {
        String[] args = body.split("&");
        String username = args[0].split("=")[1];
        String password = args[1].split("=")[1];
        return new Credentials(username, password);
    }

    private Optional<Cookie> getCookieByName(Set<Cookie> cookies, String name) {
        return cookies.stream()
                .filter(cookie -> cookie.getName().equals(name))
                .findFirst();
    }

    private void setZeroCookie(HttpResponse.Builder builder, Cookie cookie) {
        Cookie zeroCookie = new Cookie.Builder(cookie.getName(), cookie.getValue())
                .withMaxAge(0)
                .build();
        builder.withCookie(zeroCookie);
    }
}
