package ru.ramprox.server.util;

public class ContentTypeResolver {

    public static final String APPLICATION_JSON = "application/json";

    public String resolve(String path) {
        if(path.endsWith(".html")) {
            return "text/html";
        } else if(path.endsWith(".css")) {
            return "text/css";
        } else if(path.endsWith(".js")) {
            return "text/javascript";
        } else if(path.endsWith(".ico")) {
            return "image/x-icon";
        } else {
            return APPLICATION_JSON;
        }
    }
}
