package ru.ramprox.server.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ramprox.server.annotation.Component;
import ru.ramprox.server.annotation.Handler;
import ru.ramprox.server.annotation.Inject;
import ru.ramprox.server.annotation.Value;
import ru.ramprox.server.config.PropertyName;
import ru.ramprox.server.model.*;
import ru.ramprox.server.service.interfaces.SessionService;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Класс,ответственный за аутентификацию клиента
 */
@Component(name = "authenticationHandler")
@Handler(order = 2)
class AuthenticationHandler implements RequestHandler {

    private final RequestHandler nextHandler;
    private final SessionService sessionService;

    private static final String USER_ATTRIBUTE = "User";
    private static final String REQUESTED_PAGE_ATTRIBUTE = "Requested page";

    @Value(name = PropertyName.LOGIN_PROCESSING_PAGE, defaultValue = "/login_processing")
    private String loginProcessingPage;

    @Value(name = PropertyName.MAIN_PAGE, defaultValue = "/index.html")
    private String mainPage;

    @Value(name = PropertyName.LOGIN_PAGE, defaultValue = "/login.html")
    private String loginPage;

    @Value(name = PropertyName.PAGES_REQUIRED_AUTH)
    private Set<String> pagesRequiredAuthentication = new HashSet<>();

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationHandler.class);

    @Inject
    AuthenticationHandler(SessionService sessionService, RequestHandler nextHandler) {
        this.nextHandler = nextHandler;
        this.sessionService = sessionService;
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
        if(request.getResource().equals(loginProcessingPage)) {
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
        return redirectToPage(mainPage);
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
        return redirectToPage(loginPage);
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
