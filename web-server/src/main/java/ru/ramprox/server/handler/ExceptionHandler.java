package ru.ramprox.server.handler;

import ru.ramprox.server.config.Environment;
import ru.ramprox.server.config.PropertyName;
import ru.ramprox.server.model.Request;
import ru.ramprox.server.model.RequestHeaderName;
import ru.ramprox.server.model.Response;
import ru.ramprox.server.model.ResponseHeaderName;
import ru.ramprox.server.service.ContentTypeResolver;
import ru.ramprox.server.service.ResourceResolver;
import ru.ramprox.server.service.StaticResourceReader;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Класс, обрабатывающий исключения
 */
public class ExceptionHandler {

    private final ResourceResolver resourceResolver;
    private final StaticResourceReader staticResourceReader;
    private final ContentTypeResolver contentTypeResolver;

    public ExceptionHandler(ResourceResolver resourceResolver,
                            ContentTypeResolver contentTypeResolver,
                            StaticResourceReader staticResourceReader) {
        this.resourceResolver = resourceResolver;
        this.contentTypeResolver = contentTypeResolver;
        this.staticResourceReader = staticResourceReader;
    }

    /**
     * Обработка исключения
     *
     * @param request - запрос, при котором возникло исключение
     * @param ex      - возникшее исключение
     * @return ответ на возникшее исключение
     * @throws Exception - необработанное исключение
     */
    public Response handle(Request request, Exception ex) throws Exception {
        if (ex instanceof FileNotFoundException) {
            return handleFileNotFoundException(request);
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
    private Response handleFileNotFoundException(Request request) {
        Response response = new Response();
        response.setHeader(ResponseHeaderName.Status, Response.Status.NOT_FOUND.toString());
        if (request.getHeader(RequestHeaderName.REFERER) != null) {
            response.setHeader(ResponseHeaderName.CONTENT_TYPE, ContentTypeResolver.APPLICATION_JSON);
            return response;
        }
        String notFoundPage = getNotFoundPage();
        if (notFoundPage.isEmpty()) {
            response.setHeader(ResponseHeaderName.CONTENT_TYPE, ContentTypeResolver.APPLICATION_JSON);
            return response;
        }
        String path = resourceResolver.resolve(notFoundPage);
        try {
            String content = staticResourceReader.read(path);
            response.setBody(content);
            response.setHeader(ResponseHeaderName.CONTENT_TYPE, contentTypeResolver.resolve(path) + "; charset=utf-8");
        } catch (IOException e) {
            response.setHeader(ResponseHeaderName.CONTENT_TYPE, ContentTypeResolver.APPLICATION_JSON);
        }
        return response;
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