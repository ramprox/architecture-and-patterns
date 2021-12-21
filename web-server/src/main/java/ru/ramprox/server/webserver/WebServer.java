package ru.ramprox.server.webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ramprox.server.annotation.Component;
import ru.ramprox.server.annotation.Inject;
import ru.ramprox.server.annotation.Value;
import ru.ramprox.server.config.PropertyName;
import ru.ramprox.server.dispatcher.DispatcherRequest;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
class WebServer implements Server {

    private final DispatcherRequest dispatcherRequest;
    private ServerSocket serverSocket;
    private final ExecutorService executorService;

    @Value(name = PropertyName.SERVER_PORT, defaultValue = "8080")
    private String port;

    private static final Logger logger = LoggerFactory.getLogger(WebServer.class);

    @Inject
    WebServer(DispatcherRequest dispatcherRequest) {
        this.dispatcherRequest = dispatcherRequest;
        executorService = Executors.newCachedThreadPool();
    }

    /**
     * Старт веб-сервера.
     * 1. Инициализация ServerSocket.
     * 2. Как только клиент подключается происходит обработка запроса в отдельном потоке
     */
    public void start() {
        try {
            int port = Integer.parseInt(this.port);
            serverSocket = new ServerSocket(port);
            logger.info("Server started on port: {}!", port);
            while (true) {
                Socket socket = serverSocket.accept();
                logger.info("New client connected: {}!", socket.getInetAddress());
                executorService.submit(() -> dispatcherRequest.dispatchRequest(socket));
            }
        } catch (NumberFormatException ex) {
            logger.error("Invalid port value: {}", ex.getMessage());
        } catch (SocketException ex) {
            logger.info("Server interrupted: {}", ex.getMessage());
        } catch (IOException ex) {
            logger.error("Error server initialization: {}", ex.getMessage());
        } finally {
            executorService.shutdown();
            logger.info("Server stopped!");
        }
    }

    /**
     * Остановка сервера
     */
    public void stop() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException ex) {
            logger.error("Error during server close: {}", ex.getMessage());
        }
    }

    public boolean isStarted() {
        return serverSocket != null && !serverSocket.isClosed();
    }
}