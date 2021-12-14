package ru.ramprox.server.service.simpleserviceimpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ramprox.server.service.interfaces.Channel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Класс инкапсулирующий канал для чтения запроса и отправки ответа клиенту
 */
class SocketChannel implements Channel {

    private final BufferedReader in;
    private final OutputStream out;

    private static final Logger logger = LoggerFactory.getLogger(SocketChannel.class);

    SocketChannel(Socket socket) throws IOException {
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream(),
                    StandardCharsets.UTF_8));
            this.out = socket.getOutputStream();
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
        readHeaders(result);
        readPayload(result);
        return result;
    }

    /**
     * Чтение заголовков
     *
     * @param queue - очередь куда записываются результаты чтения
     * @throws IOException
     */
    private void readHeaders(Queue<String> queue) throws IOException {
        do {
            String line = in.readLine();
            logger.info(line);
            queue.add(line);
            if (line.equals("")) {
                break;
            }
        } while (in.ready());
    }

    /**
     * Чтение тела запроса
     *
     * @param queue - очередь куда записываются результаты чтения
     * @throws IOException
     */
    private void readPayload(Queue<String> queue) throws IOException {
        StringBuilder payload = null;
        int n;
        while (in.ready()) {
            n = in.read();
            if(payload == null) {
                payload = new StringBuilder();
            }
            payload.append((char) n);
        }
        if (payload != null && payload.length() > 0) {
            String[] lines = payload.toString().split(" ");
            Arrays.stream(lines).forEach(queue::add);
        }
    }

    /**
     * Отправка ответа клиенту в виде строки
     *
     * @param response - отправляемый ответ
     */
    public void sendResponse(String response) throws IOException {
        sendResponse(response.getBytes());
    }

    /**
     * Отправка ответа клиенту в виде байтов
     *
     * @param response - отправляемый ответ
     * @throws IOException
     */
    public void sendResponse(byte[] response) throws IOException {
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
        try {
            if (out != null) {
                out.close();
            }
        } catch (IOException ex) {
            logger.error("Error close output writer: {}", ex.getMessage());
        }
    }
}
