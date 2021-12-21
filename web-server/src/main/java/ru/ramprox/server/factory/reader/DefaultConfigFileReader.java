package ru.ramprox.server.factory.reader;

import ru.ramprox.server.factory.Utils;
import ru.ramprox.server.config.Prefixes;

import java.util.Map;

public class DefaultConfigFileReader implements ConfigReader {

    private static final String defaultConfigFileLocation =
            Prefixes.CLASSPATH + "/application.properties";

    @Override
    public Map<String, String> read() {
        return Utils.readFromFile(DefaultConfigFileReader.class.getClassLoader().getResource("application.properties"));
    }

    protected String getDefaultConfigFileLocation() {
        return defaultConfigFileLocation;
    }
}