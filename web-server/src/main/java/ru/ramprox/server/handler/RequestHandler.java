package ru.ramprox.server.handler;

import ru.ramprox.server.model.Request;
import ru.ramprox.server.model.Response;
import ru.ramprox.server.model.ResponseHeaderName;
import ru.ramprox.server.service.ContentTypeResolver;
import ru.ramprox.server.service.ResourceResolver;
import ru.ramprox.server.service.StaticResourceReader;

import java.io.IOException;

/**
 * Класс обработчика запросов
 */
public class RequestHandler implements Handler {

    private final ResourceResolver resourceResolver;
    private final ContentTypeResolver contentTypeResolver;
    private final StaticResourceReader staticResourceReader;

    public RequestHandler(ResourceResolver resourceResolver,
                          ContentTypeResolver contentTypeResolver,
                          StaticResourceReader staticResourceReader) {
        this.resourceResolver = resourceResolver;
        this.contentTypeResolver = contentTypeResolver;
        this.staticResourceReader = staticResourceReader;
    }

    /**
     * Обработка принятого запроса
     * 1. Определяется путь к ресурсу при помощи объекта типа ResourceResolver
     * 2. Происходит чтение запрошенного ресурса
     * 3. Формирование полного ответа клиенту при помощи объекта типа ResponseBuilder
     *
     * @param request - принятый запрос
     * @return объект типа String, содержащий полный ответ клиенту, включая заголовки и
     * содержимое статического ресурса, если он найден
     * @throws IOException - возникает при ошибках во время чтения статического ресурса
     */
    public void handleRequest(Request request, Response response) throws IOException {
        if (request.getType() == Request.RequestType.GET) {
            handleGetRequest(request, response);
        }
    }

    /**
     * Обработка GET запроса
     *
     * @param request объект типа Request, инкапсулирующий GET запрос
     * @return объект типа String, содержащий полный ответ клиенту, включая заголовки и
     * содержимое статического ресурса, если он найден
     * @throws IOException - возникает при ошибках во время чтения статического ресурса
     */
    private void handleGetRequest(Request request, Response response) throws IOException {
        String pathToResource = resourceResolver.resolve(request.getRequestedResource());
        String contentType = contentTypeResolver.resolve(pathToResource);
        Object body = null;
        if(contentType.contains("text")) {
            body = staticResourceReader.read(pathToResource);
            response.setHeader(ResponseHeaderName.CONTENT_TYPE, contentType + "; charset=utf-8");
        } else if (contentType.contains("image")) {
            body = staticResourceReader.readBytes(pathToResource);
            response.setHeader(ResponseHeaderName.CONTENT_TYPE, contentType);
        }
        response.setHeader(ResponseHeaderName.Status, Response.Status.OK.toString());
        response.setBody(body);
    }
}