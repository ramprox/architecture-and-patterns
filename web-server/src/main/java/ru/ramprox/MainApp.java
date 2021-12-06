package ru.ramprox;

import ru.ramprox.server.WebServer;

public class MainApp {
    /**
     * Точка входа в приложение
     * Запуск сервера
     * @param args - аргументы командной строки
     */
    public static void main(String[] args) {
        new WebServer().start();
    }
}
