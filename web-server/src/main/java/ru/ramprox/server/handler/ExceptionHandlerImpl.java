package ru.ramprox.server.handler;

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

    ExceptionHandlerImpl(ResourceResolver resourceResolver,
                         ContentTypeResolver contentTypeResolver,
                         ResourceReader resourceReader) {
        this.resourceResolver = resourceResolver;
        this.contentTypeResolver = contentTypeResolver;
        this.resourceReader = resourceReader;
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
    public void handle(HttpRequest request, HttpResponse.Builder responseBuilder, Exception ex) throws Exception {
        if (ex instanceof FileNotFoundException) {
            handleFileNotFoundException(request, responseBuilder);
        }
        throw ex;
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
    private void handleFileNotFoundException(HttpRequest request, HttpResponse.Builder responseBuilder) {
        responseBuilder.withHeader(ResponseHeaderName.Status, HttpResponseStatus.NOT_FOUND);
        if (request.getHeader(RequestHeaderName.REFERER) != null) {
            return;
        }
        String notFoundPage = getNotFoundPage();
        if (notFoundPage.isEmpty()) {
            return;
        }
        String path = resourceResolver.resolve(notFoundPage);
        try {
            String body = resourceReader.read(path);
            responseBuilder
                    .withBody(body)
                    .withHeader(ResponseHeaderName.CONTENT_TYPE,
                            contentTypeResolver.resolve(path) + "; charset=utf-8");
        } catch (IOException e) {

        }
    }

    /**
     * Страница для отправки клиенту, если ресурс не найден
     *
     * @return объект типа String (Например, "/414.html"),
     * содержащий ресурс для отправки клиенту если не найден запрашиваемый ресурс
     */
    private String getNotFoundPage() {
        String notFoundPage = Environment.getProperty(PropertyName.PAGE_NOT_FOUND);
        if (notFoundPage != null) {
            return "/" + notFoundPage;
        }
        return "";
    }
}