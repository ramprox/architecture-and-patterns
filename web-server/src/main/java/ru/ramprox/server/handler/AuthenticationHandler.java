package ru.ramprox.server.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ramprox.server.config.Environment;
import ru.ramprox.server.config.PropertyName;
import ru.ramprox.server.model.*;
import ru.ramprox.server.service.SessionService;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Класс,ответственный за аутентификацию клиента
 */
public class AuthenticationHandler implements Handler {

    private final Handler nextHandler;
    private final SessionService sessionService;

    private final Set<String> pagesRequiredAuthentication = new HashSet<>();
    private static final String LOGIN_PROCESSING = "/login_processing";

    private static final String USER_ATTRIBUTE = "User";
    private static final String REQUESTED_PAGE_ATTRIBUTE = "Requested page";

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationHandler.class);

    public AuthenticationHandler(SessionService sessionService, Handler nextHandler) {
        this.nextHandler = nextHandler;
        this.sessionService = sessionService;
        String[] authPages = Environment.getProperty(PropertyName.PAGES_REQUIRED_AUTH).split(";");
        Arrays.stream(authPages).forEach(page -> pagesRequiredAuthentication.add("/" + page));
    }

    /**
     * Обработка запроса
     *
     * @param request
     * @param response
     * @throws Exception
     */
    public void handleRequest(Request request, Response response) throws Exception {
        try {
            if (request.getRequestedResource().equals(LOGIN_PROCESSING)) {
                loginProcessing(request, response);
                return;
            }
            if (pagesRequiredAuthentication.contains(request.getRequestedResource())) {
                handleRequestRequiredAuthentication(request, response);
            } else {
                nextHandler.handleRequest(request, response);
            }
        } catch (IllegalArgumentException ex) {
            logger.error("Can't parse cookie header from request {}", ex.getMessage());
        }
    }

    /**
     * Обработка аутентификации
     *
     * @param request
     * @param response
     * @throws Exception
     */
    private void loginProcessing(Request request, Response response) throws Exception {
        String body = request.getBody();
        Credentials credentials = parseToCredentials(body);
        User user = new User(credentials.getUsername(), true);
        String cookieInRequest = request.getHeader(RequestHeaderName.COOKIE);
        UUID uuid = parseToUUID(cookieInRequest);
        Session session = sessionService.getSession(uuid);
        session.addAttribute(USER_ATTRIBUTE, user);
        redirectToRequestedPage(request, response, session);
    }

    /**
     * ОБработка запросов, требующих аутентификации
     *
     * @param request
     * @param response
     * @throws Exception
     */
    private void handleRequestRequiredAuthentication(Request request, Response response) throws Exception {
        String cookieInRequest = request.getHeader(RequestHeaderName.COOKIE);
        if (cookieInRequest == null) {
            handleWithoutCookieInRequest(request, response);
            return;
        }
        UUID uuid = parseToUUID(cookieInRequest);
        Session session = sessionService.getSession(uuid);
        User user = (User) session.getAttribute(USER_ATTRIBUTE);
        if (user != null && user.isAuthenticated()) {
            handleUserAuthenticated(request, response, session);
            Object body = response.getContent();
            if (body instanceof String) {
                String bodyStr = (String)body;
                response.setBody(bodyStr.replaceAll("principal", user.getName()));
            }
        } else {
            session.addAttribute(REQUESTED_PAGE_ATTRIBUTE, request.getRequestedResource());
            redirectToLoginPage(response);
        }
    }

    /**
     * Обработка запроса, если в нем нет Cookies
     * @param request
     * @param response
     */
    private void handleWithoutCookieInRequest(Request request, Response response) {
        Cookie cookieInResponse = (Cookie) response.getHeader(ResponseHeaderName.SET_COOKIE);
        UUID uuid = UUID.fromString(cookieInResponse.getValue());
        Session session = sessionService.getSession(uuid);
        session.addAttribute(REQUESTED_PAGE_ATTRIBUTE, request.getRequestedResource());
        redirectToLoginPage(response);
    }

    /**
     * Обработка запроса клиента, прошедшего аутентификацию
     * @param request
     * @param response
     * @param session
     * @throws Exception
     */
    private void handleUserAuthenticated(Request request, Response response, Session session) throws Exception {
        String requestedResource = (String) session.deleteAttribute(REQUESTED_PAGE_ATTRIBUTE);
        if (requestedResource != null) {
            request.setRequestedResource(requestedResource);
        }
        nextHandler.handleRequest(request, response);
    }

    /**
     * Парсинг строки в UUID
     * @param cookieHeader
     * @return
     */
    private static UUID parseToUUID(String cookieHeader) {
        String[] args = cookieHeader.split("=");
        return UUID.fromString(args[1]);
    }

    /**
     * Перенаправление в страницу логина
     * @param response
     */
    private void redirectToLoginPage(Response response) {
        response.setHeader(ResponseHeaderName.LOCATION, "/login.html");
        response.setHeader(ResponseHeaderName.Status, Response.Status.REDIRECT);
    }

    /**
     * Перенаправление в запрошенную страницу после аутентификации
     * @param request
     * @param response
     * @param session
     * @throws Exception
     */
    private void redirectToRequestedPage(Request request, Response response, Session session) throws Exception {
        response.setHeader(ResponseHeaderName.LOCATION, session.deleteAttribute(REQUESTED_PAGE_ATTRIBUTE));
        response.setHeader(ResponseHeaderName.Status, Response.Status.REDIRECT);
    }

    /**
     * Парсинг лагина и пароля из тела запроса
     * @param body
     * @return
     */
    private Credentials parseToCredentials(String body) {
        String[] args = body.split("&");
        String username = args[0].split("=")[1];
        String password = args[1].split("=")[1];
        return new Credentials(username, password);
    }
}
