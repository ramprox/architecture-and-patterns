package ru.ramprox.server.service.interfaces;

import java.io.IOException;
import java.util.Queue;

public interface Channel extends AutoCloseable {
    Queue<String> readRequest() throws IOException;
    void sendResponse(String response) throws IOException;
    void sendResponse(byte[] response) throws IOException;
}
