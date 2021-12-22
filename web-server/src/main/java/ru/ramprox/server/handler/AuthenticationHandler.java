package ru.ramprox.server.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ramprox.server.annotation.Handler;
import ru.ramprox.server.config.Environment;
import ru.ramprox.server.config.PropertyName;
import ru.ramprox.server.model.*;
import ru.ramprox.server.service.interfaces.SessionService;

import java.util.*;

/**
 * Класс,ответственный за аутентификацию клиента
 */
@Handler(order = 2)
class AuthenticationHandler implements RequestHandler {

    private final RequestHandler nextHandler;
    private final SessionService sessionService;

    private static final String USER_ATTRIBUTE = "User";
    private static final String REQUESTED_PAGE_ATTRIBUTE = "Requested page";

    private static final String LOGIN_PROCESSING_PAGE = "/login_processing";
    private static final String MAIN_PAGE = "/index.html";
    private static final String LOGIN_PAGE = "/login.html";
    private Set<String> pagesRequiredAuthentication = new HashSet<>();

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationHandler.class);

    AuthenticationHandler(SessionService sessionService, RequestHandler nextHandler) {
        this.nextHandler = nextHandler;
        this.sessionService = sessionService;
        String[] authPages = Environment.getProperty(PropertyName.PAGES_REQUIRED_AUTH).split(";");
        Arrays.stream(authPages).forEach(page -> pagesRequiredAuthentication.add(page));
    }

    /**
     * Обработка запроса
     *
     * @param request         - Принятый запрос
     * @throws Exception - ошибка обработки
     */
    @Override
    public HttpResponse handle(HttpRequest request) throws Exception {

        Optional<Cookie> cookie = getCookieByName(request.getCookies(), Cookie.JSESSION_ID);
        if(request.getResource().equals(LOGIN_PROCESSING_PAGE)) {
            return loginProcessing(request, cookie.get());
        }
        if(pagesRequiredAuthentication.contains(request.getResource())) {
            return handleRequestRequiredAuthentication(request, cookie.get());
        }
        return nextHandler.handle(request);
    }

    /**
     * Обработка аутентификации
     *
     * @param request         - Запрос, содержащий credentials
     */
    private HttpResponse loginProcessing(HttpRequest request, Cookie cookie) {
        String body = request.getBody();
        Credentials credentials = convertToCredentials(body);
        User user = new User(credentials.getUsername(), true);
        UUID uuid = UUID.fromString(cookie.getValue());
        Session session = sessionService.getSession(uuid);
        session.addAttribute(USER_ATTRIBUTE, user);
        String requestedPage = (String) session.deleteAttribute(REQUESTED_PAGE_ATTRIBUTE);
        if (requestedPage != null) {
            return redirectToPage(requestedPage);
        }
        return redirectToPage(MAIN_PAGE);
    }

    /**
     * Обработка запросов, требующих аутентификацию
     *
     * @param request         - Запрос, требующий аутентификацию
     * @throws Exception - ошибка обработки
     */
    private HttpResponse handleRequestRequiredAuthentication(HttpRequest request, Cookie cookie) throws Exception {
        UUID uuid = UUID.fromString(cookie.getValue());
        Session session = sessionService.getSession(uuid);
        User user = (User) session.getAttribute(USER_ATTRIBUTE);
        if (user != null && user.isAuthenticated()) {
            return nextHandler.handle(request);
        }
        session.addAttribute(REQUESTED_PAGE_ATTRIBUTE, request.getResource());
        return redirectToPage(LOGIN_PAGE);
    }

    private HttpResponse redirectToPage(String page) {
        return new HttpResponse.Builder()
                .withHeader(ResponseHeaderName.LOCATION, page)
                .withStatus(HttpResponseStatus.REDIRECT)
                .build();
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
}
