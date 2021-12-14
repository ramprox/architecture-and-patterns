package ru.ramprox.server.service.simpleserviceimpl;

import ru.ramprox.server.service.interfaces.ContentTypeResolver;

import java.util.HashMap;
import java.util.Map;

class ContentTypeResolverImpl implements ContentTypeResolver {

    private static final String APPLICATION_JSON = "application/json";

    private final Map<String, String> textContentTypes = new HashMap<>();
    private final Map<String, String> imageContentTypes = new HashMap<>();

    ContentTypeResolverImpl() {
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

    @Override
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
