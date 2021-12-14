package ru.ramprox.server.dispatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ramprox.server.handler.*;
import ru.ramprox.server.model.HttpRequest;
import ru.ramprox.server.model.HttpResponse;
import ru.ramprox.server.service.interfaces.Channel;
import ru.ramprox.server.service.interfaces.RequestParser;
import ru.ramprox.server.service.interfaces.ResponseConverter;
import ru.ramprox.server.service.simpleserviceimpl.ServiceFactory;

import java.io.IOException;
import java.net.Socket;
import java.util.Queue;

class DispatcherRequestImpl implements DispatcherRequest {

    private final RequestParser requestParser;
    private final ExceptionHandler exceptionHandler;
    private final ResponseConverter responseConverter;
    private final RequestHandler handler;
    private final ServiceFactory serviceFactory;

    private static final Logger logger = LoggerFactory.getLogger(DispatcherRequestImpl.class);

    DispatcherRequestImpl(RequestParser requestParser,
                          ResponseConverter responseConverter,
                          ExceptionHandler exceptionHandler,
                          RequestHandler handler, ServiceFactory serviceFactory) {
        this.requestParser = requestParser;
        this.responseConverter = responseConverter;
        this.exceptionHandler = exceptionHandler;
        this.handler = handler;
        this.serviceFactory = serviceFactory;
    }

    @Override
    public void dispatchRequest(Socket socket) {
        try (Channel channel = serviceFactory.getChannel(socket)) {
            Queue<String> stringRequest = channel.readRequest();
            if (stringRequest != null) {
                HttpResponse response = handleRequest(stringRequest);
                String stringResponseHeader = responseConverter.convert(response);
                channel.sendResponse(stringResponseHeader);
                Object body = response.getBody();
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

    private HttpResponse handleRequest(Queue<String> stringRequest) throws Exception {
        HttpRequest request = requestParser.parseRequest(stringRequest);
        HttpResponse.Builder responseBuilder = new HttpResponse.Builder();
        try {
            handler.handle(request, responseBuilder);
        } catch (Exception ex) {
            exceptionHandler.handle(request,responseBuilder, ex);
        }
        return responseBuilder.build();
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