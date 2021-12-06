package ru.ramprox.server.handler;

import ru.ramprox.server.model.Request;
import ru.ramprox.server.model.Response;
import ru.ramprox.server.util.ContentTypeResolver;
import ru.ramprox.server.util.ResourceResolver;
import ru.ramprox.server.util.ResponseToStringBuilder;
import ru.ramprox.server.util.StaticResourceReader;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Класс обработчика запросов
 */
public class RequestHandler {

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
    public Response handleRequest(Request request) throws IOException {
        Response response = new Response();
        if (request.getType() == Request.RequestType.GET) {
            return handleGetRequest(request);
        }
        return response;
    }

    /**
     * Обработка GET запроса
     *
     * @param request объект типа Request, инкапсулирующий GET запрос
     * @return объект типа String, содержащий полный ответ клиенту, включая заголовки и
     * содержимое статического ресурса, если он найден
     * @throws IOException - возникает при ошибках во время чтения статического ресурса
     */
    private Response handleGetRequest(Request request) throws IOException {
        Response response = new Response();
        String pathToResource = resourceResolver.resolve(request.getRequestedResource());
        String content = staticResourceReader.read(pathToResource);
        response.setStatus("200 OK");
        response.setContent(content);
        response.setContentType(contentTypeResolver.resolve(pathToResource));
        return response;
    }

}
