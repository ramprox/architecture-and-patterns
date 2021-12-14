package ru.ramprox.server.service.simpleserviceimpl;

import ru.ramprox.server.service.interfaces.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.net.URL;
import java.util.*;
import java.util.jar.Manifest;
import java.util.stream.Collectors;

public class ServiceFactory {

    private final Map<Class<?>, Object> objects;

    public ServiceFactory() {
        objects = initialize();
    }

    protected Map<Class<?>, Object> initialize() {
        Map<Class<?>, Object> objects = new HashMap<>();
        objects.put(ResourceResolver.class, new ResourceResolverImpl());
        objects.put(ResourceReader.class, new StaticResourceReader());
        objects.put(ContentTypeResolver.class, new ContentTypeResolverImpl());
        objects.put(RequestParser.class, new RequestParserImpl());
        objects.put(ResponseConverter.class, new ResponseHeaderConverter());
        objects.put(SessionService.class, new SessionServiceImpl());
        return objects;
    }

    public <T> T get(Class<T> cl) {
        return cl.cast(objects.get(cl));
    }

    public Channel getChannel(Socket socket) throws IOException {
        return new SocketChannel(socket);
    }
}
