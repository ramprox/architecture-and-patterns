package ru.ramprox.server.handler;

import ru.ramprox.server.annotation.Component;
import ru.ramprox.server.annotation.Handler;
import ru.ramprox.server.annotation.Inject;
import ru.ramprox.server.model.*;
import ru.ramprox.server.service.interfaces.ContentTypeResolver;
import ru.ramprox.server.service.interfaces.ResourceReader;
import ru.ramprox.server.service.interfaces.ResourceResolver;

import java.io.IOException;

/**
 * Класс обработчика запросов
 */
@Component(name = "mainHandler")
@Handler(order = 3)
class MainHandler implements RequestHandler {

    private final ResourceResolver resourceResolver;
    private final ContentTypeResolver contentTypeResolver;
    private final ResourceReader resourceReader;

    @Inject
    MainHandler(ResourceResolver resourceResolver,
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
    public HttpResponse handle(HttpRequest request) throws IOException {
        if (request.getType() == HttpRequest.RequestType.GET) {
            return handleGetRequest(request);
        }
        return new HttpResponse.Builder()
                .withStatus(HttpResponseStatus.METHOD_NOT_ALLOWED)
                .build();
    }

    /**
     * Обработка GET запроса
     *
     * @param request объект типа HttpRequest, инкапсулирующий GET запрос
     * @return объект типа String, содержащий полный ответ клиенту, включая заголовки и
     * содержимое статического ресурса, если он найден
     * @throws IOException - возникает при ошибках во время чтения статического ресурса
     */
    private HttpResponse handleGetRequest(HttpRequest request) throws IOException {
        String pathToResource = resourceResolver.resolve(request.getResource());
        String contentType = contentTypeResolver.resolve(pathToResource);
        Object body = null;
        HttpResponse.Builder builder = new HttpResponse.Builder();
        if (contentType.contains("text")) {
            body = resourceReader.read(pathToResource);
            builder.withHeader(ResponseHeaderName.CONTENT_TYPE, contentType + "; charset=utf-8");
        } else if (contentType.contains("image")) {
            body = resourceReader.readBytes(pathToResource);
            builder.withHeader(ResponseHeaderName.CONTENT_TYPE, contentType);
        }
        return builder
                .withStatus(HttpResponseStatus.OK)
                .withBody(body)
                .build();
    }
}