package ru.ramprox.server.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ramprox.server.config.Environment;
import ru.ramprox.server.config.PropertyName;
import ru.ramprox.server.model.*;
import ru.ramprox.server.service.interfaces.ContentTypeResolver;
import ru.ramprox.server.service.interfaces.ResourceReader;
import ru.ramprox.server.service.interfaces.ResourceResolver;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Класс, обрабатывающий исключения
 */
class ExceptionHandlerImpl implements ExceptionHandler {

    private final ResourceResolver resourceResolver;
    private final ResourceReader resourceReader;
    private final ContentTypeResolver contentTypeResolver;

    private String pageNotFound;

    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandlerImpl.class);

    ExceptionHandlerImpl(ResourceResolver resourceResolver,
                         ContentTypeResolver contentTypeResolver,
                         ResourceReader resourceReader) {
        this.resourceResolver = resourceResolver;
        this.contentTypeResolver = contentTypeResolver;
        this.resourceReader = resourceReader;
        String propertyPageNotFound = Environment.getProperty(PropertyName.PAGE_NOT_FOUND);
        pageNotFound = propertyPageNotFound != null ? propertyPageNotFound : "";
    }

    /**
     * Обработка исключения
     *
     * @param request - запрос, при котором возникло исключение
     * @param ex      - возникшее исключение
     * @return ответ на возникшее исключение
     * @throws Exception - необработанное исключение
     */
    @Override
    public HttpResponse handle(HttpRequest request, Exception ex) {
        if (ex instanceof FileNotFoundException) {
            return handleFileNotFoundException(request);
        }
        return new HttpResponse.Builder()
                .withStatus(HttpResponseStatus.METHOD_NOT_ALLOWED)
                .build();
    }

    /**
     * Обработка FileNotFoundException
     * Если запрашиваемый ресурс (Например, "style.css") является результатом запроса из другого ресурса
     * (Например, если html страница содержит следующее:
     * <head>
     * <link ref="stylesheet" href="style.css">
     * ...
     * </head>
     * ...
     * что отражается в заголовке Referer запроса),
     * то страница notFoundPage не возвращается, даже если она есть
     *
     * @param request - Запрос, при котором возникло данное исключение
     * @return ответ на возникшее исключение
     */
    private HttpResponse handleFileNotFoundException(HttpRequest request) {

        HttpResponse.Builder builder = new HttpResponse.Builder();
        builder.withStatus(HttpResponseStatus.NOT_FOUND);
        if (request.getHeader(RequestHeaderName.REFERER) != null || pageNotFound.isEmpty()) {
            return builder.build();
        }
        String path = resourceResolver.resolve(pageNotFound);
        try {
            String body = resourceReader.read(path);
            builder
                    .withBody(body)
                    .withHeader(ResponseHeaderName.CONTENT_TYPE,
                            contentTypeResolver.resolve(path) + "; charset=utf-8");
        } catch (IOException e) {
            logger.error("Error read 'Not found' page {}: {}", pageNotFound, e.getMessage());
        }
        return builder.build();
    }
}