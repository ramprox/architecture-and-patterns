package ru.ramprox.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.ramprox.server.config.Environment;
import ru.ramprox.server.config.PropertyName;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class WebServer {

    private final DispatcherRequest dispatcherRequest;

    private static final Logger logger = LogManager.getLogger(WebServer.class);

    public WebServer() {
        dispatcherRequest = new DispatcherRequest();
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
            logger.error("Error server initializing: {}", ex.getMessage());
        }
    }
}
