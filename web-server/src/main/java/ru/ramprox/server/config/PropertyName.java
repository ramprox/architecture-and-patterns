package ru.ramprox.server.config;

/**
 * Класс, содержащий названия настроек приложения
 */
public interface PropertyName {

    String CONFIG_LOCATION = "config.location";
    String SERVER_PORT = "server.port";
    String PATH_TO_STATIC = "path.to.static";
    String PAGE_NOT_FOUND = "page.not.found";
    String PATH_TO_SESSIONS = "path.to.sessions";
    String PAGES_REQUIRED_AUTH = "pages.required.auth";
    String PACKAGES_TO_SCAN = "packages.to.scan";
    String LOGIN_PAGE = "login.page";
    String MAIN_PAGE = "main.page";
    String LOGIN_PROCESSING_PAGE = "login.processing.page";
}