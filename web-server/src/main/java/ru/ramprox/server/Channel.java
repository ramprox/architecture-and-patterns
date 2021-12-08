package ru.ramprox.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Класс инкапсулирующий канал для чтения запроса и отправки ответа клиенту
 */
public class Channel implements AutoCloseable {

    private final BufferedReader in;
    private final PrintWriter out;

    private static final Logger logger = LogManager.getLogger(Channel.class);

    public Channel(Socket socket) throws IOException {
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException ex) {
            logger.error("Error channel initialization: {}", ex.getMessage());
            throw ex;
        }
    }

    /**
     * Чтение запроса
     *
     * @return объект типа String, содержащий запрос
     * @throws IOException - при ошибках чтения
     */
    public Queue<String> readRequest() throws IOException {
        while (!in.ready()) ;
        Queue<String> result = new LinkedList<>();
        while (in.ready()) {
            String line = in.readLine();
            logger.info(line);
            result.add(line);
        }
        return result;
    }

    /**
     * Отправка ответа клиенту
     *
     * @param response - отправляемый ответ
     */
    public void sendResponse(String response) {
        out.write(response);
        out.flush();
    }

    /**
     * Закрытие канала
     */
    public void close() {
        try {
            if (in != null) {
                in.close();
            }
        } catch (IOException ex) {
            logger.error("Error close input reader: {}", ex.getMessage());
        }
        if (out != null) {
            out.close();
        }
    }
}
