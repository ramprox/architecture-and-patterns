package ru.ramprox.server.handler;

import ru.ramprox.server.model.*;
import ru.ramprox.server.service.interfaces.ContentTypeResolver;
import ru.ramprox.server.service.interfaces.ResourceReader;
import ru.ramprox.server.service.interfaces.ResourceResolver;

import java.io.IOException;

/**
 * Класс обработчика запросов
 */
class LastHandler implements RequestHandler {

    private final ResourceResolver resourceResolver;
    private final ContentTypeResolver contentTypeResolver;
    private final ResourceReader resourceReader;

    LastHandler(ResourceResolver resourceResolver,
                ContentTypeResolver contentTypeResolver,
                ResourceReader resourceReader) {
        this.resourceResolver = resourceResolver;
        this.contentTypeResolver = contentTypeResolver;
        this.resourceReader = resourceReader;
    }

    /**
     * Обработка принятого запроса
     * 1. Определяется путь к ресурсу при помощи объекта типа ResourceResolverImpl
     * 2. Происходит чтение запрошенного ресурса
     * 3. Формирование полного ответа клиенту при помощи объекта типа ResponseBuilder
     *
     * @param request - принятый запрос
     * @return объект типа String, содержащий полный ответ клиенту, включая заголовки и
     * содержимое статического ресурса, если он найден
     * @throws IOException - возникает при ошибках во время чтения статического ресурса
     */
    public void handle(HttpRequest request, HttpResponse.Builder responseBuilder) throws IOException {
        if (request.getType() == HttpRequest.RequestType.GET) {
            handleGetRequest(request, responseBuilder);
        }
    }

    /**
     * Обработка GET запроса
     *
     * @param request объект типа HttpRequest, инкапсулирующий GET запрос
     * @return объект типа String, содержащий полный ответ клиенту, включая заголовки и
     * содержимое статического ресурса, если он найден
     * @throws IOException - возникает при ошибках во время чтения статического ресурса
     */
    private void handleGetRequest(HttpRequest request, HttpResponse.Builder responseBuilder) throws IOException {
        String pathToResource = resourceResolver.resolve(request.getResource());
        String contentType = contentTypeResolver.resolve(pathToResource);
        Object body = null;
        if (contentType.contains("text")) {
            body = resourceReader.read(pathToResource);
            responseBuilder.withHeader(ResponseHeaderName.CONTENT_TYPE, contentType + "; charset=utf-8");
        } else if (contentType.contains("image")) {
            body = resourceReader.readBytes(pathToResource);
            responseBuilder.withHeader(ResponseHeaderName.CONTENT_TYPE, contentType);
        }
        responseBuilder
                .withStatus(HttpResponseStatus.OK)
                .withBody(body);
    }
}