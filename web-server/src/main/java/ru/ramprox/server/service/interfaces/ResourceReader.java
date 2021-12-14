package ru.ramprox.server.service.interfaces;

import java.io.IOException;

public interface ResourceReader {
    String read(String path) throws IOException;
    byte[] readBytes(String path) throws IOException;
}
