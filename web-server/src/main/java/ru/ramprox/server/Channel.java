package ru.ramprox.server;

import java.io.*;
import java.net.Socket;

/**
 * Класс инкапсулирующий канал для чтения запроса и отправки ответа клиенту
 */
public class Channel implements AutoCloseable {

    private final BufferedReader in;
    private final PrintWriter out;

    public Channel(Socket socket) throws IOException {
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException ex) {
            System.out.printf("Error channel initialization: %s", ex.getMessage());
            throw ex;
        }
    }

    /**
     * Чтение запроса
     *
     * @return объект типа String, содержащий запрос
     * @throws IOException - при ошибках чтения
     */
    public String readRequest() throws IOException {
        while(!in.ready());
        StringBuilder builder = new StringBuilder();
        while (in.ready()) {
            String line = in.readLine();
            builder.append(line).append("\n");
            System.out.println(line);
        }
        return builder.toString();
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
            ex.printStackTrace();
        }
        if (out != null) {
            out.close();
        }
    }
}
