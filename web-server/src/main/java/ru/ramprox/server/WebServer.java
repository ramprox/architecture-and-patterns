package ru.ramprox.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ramprox.server.config.Environment;
import ru.ramprox.server.config.PropertyName;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class WebServer {

    private final DispatcherRequest dispatcherRequest;

    private static final Logger logger = LoggerFactory.getLogger(WebServer.class);

    public WebServer() {
        this.dispatcherRequest = new DispatcherRequest();
    }

    /**
     * Старт веб-сервера.
     * 1. Инициализация ServerSocket.
     * 2. Как только клиент подключается происходит обработка запроса в отдельном потоке
     */
    public void start() {
        int port = Integer.parseInt(Environment.getProperty(PropertyName.PORT));
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("Server started on port: {}!", port);
            while (true) {
                Socket socket = serverSocket.accept();
                logger.info("New client connected: {}!", socket.getInetAddress());
                new Thread(() -> dispatcherRequest.dispatchRequest(socket)).start();
            }
        } catch (IOException ex) {
            logger.error("Error server initialization: {}", ex.getMessage());
        }
    }

}