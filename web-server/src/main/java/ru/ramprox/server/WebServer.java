package ru.ramprox.server;

import ru.ramprox.server.config.Environment;
import ru.ramprox.server.config.PropertyName;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class WebServer {

    private final DispatcherRequest dispatcherRequest;

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
            System.out.printf("Server started on port: %d!\n", port);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.printf("New client connected: %s!\n", socket.getInetAddress());
                new Thread(() -> dispatcherRequest.dispatchRequest(socket)).start();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
