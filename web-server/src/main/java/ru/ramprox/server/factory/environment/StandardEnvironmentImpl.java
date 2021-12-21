package ru.ramprox.server.factory.environment;

import ru.ramprox.server.factory.reader.ConfigReader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StandardEnvironmentImpl implements Environment {

    private final Map<String, String> properties = new HashMap<>();

    public StandardEnvironmentImpl(List<ConfigReader> readers) {
        readers.forEach(reader -> properties.putAll(reader.read()));
    }

    @Override
    public String getProperty(String propertyName) {
        return properties.get(propertyName);
    }
}