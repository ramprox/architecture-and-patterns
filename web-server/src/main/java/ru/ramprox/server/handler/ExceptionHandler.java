package ru.ramprox.server.handler;

import ru.ramprox.server.config.Environment;
import ru.ramprox.server.config.PropertyName;
import ru.ramprox.server.model.Request;
import ru.ramprox.server.model.Response;
import ru.ramprox.server.util.ContentTypeResolver;
import ru.ramprox.server.util.ResourceResolver;
import ru.ramprox.server.util.StaticResourceReader;

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
        Response response;
        if (ex instanceof FileNotFoundException) {
            response = handleFileNotFoundException(request);
        } else {
            throw new Exception(ex);
        }
        return response;
    }

    /**
     * Обработка FileNotFoundException
     * Если запрашиваемый ресурс (Например, "style.css") является результатом предыдущего запроса (Например,
     * если html страница содержит следующее:
     * <head>
     * <link ref="stylesheet" href="style.css">
     * ...
     * </head>
     * ...
     * что отражается в заголовке Referer Запроса, то страница notFoundPage не возвращается, даже если она есть
     *
     * @param request - Запрос, при котором возникло данное исключение
     * @return ответ на возникшее исключение
     */
    private Response handleFileNotFoundException(Request request) {
        Response response = new Response();
        response.setStatus("404 NOT_FOUND");
        if (request.getReferer() != null) {
            response.setContentType(ContentTypeResolver.APPLICATION_JSON);
            return response;
        }
        String notFoundPage = getNotFoundPage();
        if (notFoundPage.isEmpty()) {
            response.setContentType(ContentTypeResolver.APPLICATION_JSON);
            return response;
        }
        String path = resourceResolver.resolve(notFoundPage);
        try {
            String content = staticResourceReader.read(path);
            response.setContent(content);
            response.setContentType(contentTypeResolver.resolve(path));
        } catch (IOException e) {
            response.setContentType(ContentTypeResolver.APPLICATION_JSON);
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
