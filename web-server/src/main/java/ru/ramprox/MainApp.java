package ru.ramprox;

import ru.ramprox.server.Application;
import ru.ramprox.server.factory.context.Context;
import ru.ramprox.server.webserver.Server;

public class MainApp {
    /**
     * Точка входа в приложение
     * Запуск приложения
     * @param args - аргументы командной строки
     */
    public static void main(String[] args) {
        Context context = Application.run(args);
        Server server = context.get(Server.class);
        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
        server.start();
    }
}
