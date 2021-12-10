package ru.ramprox.server.service;

import java.util.HashMap;
import java.util.Map;

public class ContentTypeResolver {

    public static final String APPLICATION_JSON = "application/json";

    private final Map<String, String> textContentTypes = new HashMap<>();
    private final Map<String, String> imageContentTypes = new HashMap<>();

    public ContentTypeResolver() {
        initializeContentTypes();
    }

    private void initializeContentTypes() {
        textContentTypes.put(".html", "text/html");
        textContentTypes.put(".css", "text/css");
        textContentTypes.put(".js", "text/javascript");
        imageContentTypes.put(".ico", "image/x-icon");
        imageContentTypes.put(".png", "image/png");
        imageContentTypes.put(".jpeg", "image/jpeg");
    }

    public String resolve(String path) {
        int dotIndex = path.lastIndexOf('.');
        String extension = path.substring(dotIndex);
        return findContentType(extension);
    }

    private String findContentType(String extension) {
        String result = textContentTypes.get(extension);
        if(result == null) {
            result = imageContentTypes.get(extension);
        }
        return result != null ? result : APPLICATION_JSON;
    }
}
