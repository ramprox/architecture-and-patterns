package ru.ramprox.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.ramprox.server.handler.ExceptionHandler;
import ru.ramprox.server.handler.RequestHandler;
import ru.ramprox.server.model.Request;
import ru.ramprox.server.model.Response;
import ru.ramprox.server.util.*;

import java.io.IOException;
import java.net.Socket;
import java.util.Queue;

public class DispatcherRequest {

    private final RequestHandler requestHandler;
    private final RequestParser requestParser;
    private final ExceptionHandler exceptionHandler;
    private final ResponseToStringBuilder responseToStringBuilder;

    private static final Logger logger = LogManager.getLogger(DispatcherRequest.class);

    public DispatcherRequest() {
        requestParser = new RequestParser();
        responseToStringBuilder = new ResponseToStringBuilder();
        ResourceResolver resourceResolver = new ResourceResolver();
        ContentTypeResolver contentTypeResolver = new ContentTypeResolver();
        StaticResourceReader staticResourceReader = new StaticResourceReader();
        requestHandler = new RequestHandler(resourceResolver, contentTypeResolver, staticResourceReader);
        exceptionHandler = new ExceptionHandler(resourceResolver, contentTypeResolver, staticResourceReader);
    }

    public void dispatchRequest(Socket socket) {
        try (Channel channel = new Channel(socket)) {
            Queue<String> stringRequest = channel.readRequest();
            if (stringRequest != null) {
                Response response = handleRequest(stringRequest);
                String stringResponse = responseToStringBuilder.build(response);
                channel.sendResponse(stringResponse);
            }
        } catch (Exception ex) {
            logger.error("Error handle request: {}", ex.getMessage());
        } finally {
            closeConnection(socket);
        }
    }

    private Response handleRequest(Queue<String> stringRequest) throws Exception {
        Request request = requestParser.parseRequest(stringRequest);
        Response response;
        try {
            response = requestHandler.handleRequest(request);
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
