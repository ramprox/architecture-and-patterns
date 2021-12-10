package ru.ramprox.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ramprox.server.handler.*;
import ru.ramprox.server.model.Request;
import ru.ramprox.server.model.Response;
import ru.ramprox.server.service.*;

import java.io.IOException;
import java.net.Socket;
import java.util.Queue;

public class DispatcherRequest {

    private final RequestParser requestParser;
    private final ExceptionHandler exceptionHandler;
    private final BuilderResponseHeaderToString responseToStringBuilder;
    private final Handler headHandler;

    private static final Logger logger = LoggerFactory.getLogger(DispatcherRequest.class);

    public DispatcherRequest() {
        requestParser = new RequestParser();
        responseToStringBuilder = new BuilderResponseHeaderToString();
        ResourceResolver resourceResolver = new ResourceResolver();
        ContentTypeResolver contentTypeResolver = new ContentTypeResolver();
        StaticResourceReader staticResourceReader = new StaticResourceReader();
        SessionService sessionService = new SessionService();
        exceptionHandler = new ExceptionHandler(resourceResolver, contentTypeResolver, staticResourceReader);
        headHandler = initializeHandlers(sessionService, resourceResolver, contentTypeResolver, staticResourceReader);
    }

    private Handler initializeHandlers(SessionService sessionService,
                                       ResourceResolver resourceResolver,
                                       ContentTypeResolver contentTypeResolver,
                                       StaticResourceReader staticResourceReader) {
        Handler h1 = new RequestHandler(resourceResolver,
                contentTypeResolver, staticResourceReader);
        Handler h2 = new AuthenticationHandler(sessionService, h1);
        return new SessionHandler(sessionService, h2);
    }

    public void dispatchRequest(Socket socket) {
        try (Channel channel = new Channel(socket)) {
            Queue<String> stringRequest = channel.readRequest();
            if (stringRequest != null) {
                Response response = handleRequest(stringRequest);
                String stringResponseHeader = responseToStringBuilder.build(response);
                channel.sendResponse(stringResponseHeader);
                Object body = response.getContent();
                if(body instanceof String) {
                    channel.sendResponse((String)body);
                } else if(body instanceof byte[]) {
                    channel.sendResponse((byte[])body);
                }
            }
        } catch (Exception ex) {
            logger.error("Error handle request: {}", ex.getMessage());
            ex.printStackTrace();
        } finally {
            closeConnection(socket);
        }
    }

    private Response handleRequest(Queue<String> stringRequest) throws Exception {
        Request request = requestParser.parseRequest(stringRequest);
        Response response = new Response();
        try {
            headHandler.handleRequest(request, response);
        } catch (Exception ex) {
            response = exceptionHandler.handle(request, ex);
        }
        return response;
    }

    /**
     * Закрытие соединения
     *
     * @param socket - объект типа Socket, привязанного к клиенту
     */
    private void closeConnection(Socket socket) {
        try {
            socket.close();
        } catch (IOException ex) {
            logger.error("Socket close error: {}", ex.getMessage());
        }
        logger.info("Client disconnected: {}", socket.getInetAddress());
    }
}