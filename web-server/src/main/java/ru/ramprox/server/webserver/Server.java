package ru.ramprox.server.webserver;

public interface Server {
    void start();
    void stop();
    boolean isStarted();
}
