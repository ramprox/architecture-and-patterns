package ru.ramprox.server.dispatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ramprox.server.annotation.Component;
import ru.ramprox.server.annotation.Inject;
import ru.ramprox.server.handler.*;
import ru.ramprox.server.model.HttpRequest;
import ru.ramprox.server.model.HttpResponse;
import ru.ramprox.server.service.interfaces.Channel;
import ru.ramprox.server.service.interfaces.RequestParser;
import ru.ramprox.server.service.interfaces.ResponseConverter;
import ru.ramprox.server.service.simpleserviceimpl.SocketChannel;

import java.io.IOException;
import java.net.Socket;
import java.util.Queue;

@Component
class DispatcherRequestImpl implements DispatcherRequest {

    private final RequestParser requestParser;
    private final ExceptionHandler exceptionHandler;
    private final ResponseConverter responseConverter;
    private final RequestHandler handler;

    private static final Logger logger = LoggerFactory.getLogger(DispatcherRequestImpl.class);

    @Inject
    DispatcherRequestImpl(RequestParser requestParser,
                          ResponseConverter responseConverter,
                          ExceptionHandler exceptionHandler,
                          RequestHandler handler) {
        this.requestParser = requestParser;
        this.responseConverter = responseConverter;
        this.exceptionHandler = exceptionHandler;
        this.handler = handler;
    }

    @Override
    public void dispatchRequest(Socket socket) {
        try (Channel channel = getChannel(socket)) {
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

    protected Channel getChannel(Socket socket) throws IOException {
        return new SocketChannel(socket);
    }

    private HttpResponse handleRequest(Queue<String> stringRequest) throws Exception {
        HttpRequest request = requestParser.parseRequest(stringRequest);
        HttpResponse response = null;
        try {
            response = handler.handle(request);
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