package ru.ramprox.server.factory.reader;

import ru.ramprox.server.config.PropertyName;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public abstract class CommandLineArgsReader implements ConfigReader {

    private final String[] commandLineArgs;

    public CommandLineArgsReader(String... args) {
        this.commandLineArgs = args;
    }

    @Override
    public Map<String, String> read() {
        Map<String, String> properties = parseCommandLineArgs();
        String userConfigLocation = properties.remove(PropertyName.CONFIG_LOCATION);
        if (userConfigLocation != null) {
            ConfigReader reader = getUserDefinedConfigFileReader(userConfigLocation);
            Map<String, String> userPropertiesFromFile = reader.read();
            userPropertiesFromFile.putAll(properties);
            properties = userPropertiesFromFile;
        }
        return properties;
    }

    public abstract ConfigReader getUserDefinedConfigFileReader(String userConfigFileLocation);

    /**
     * Парсинг аргументов командной строки
     *
     * @return объект типа Map<String, String>
     */
    private Map<String, String> parseCommandLineArgs() {
        Map<String, String> result = new HashMap<>();
        Arrays.stream(commandLineArgs).forEach(line -> {
            line = line.substring("--".length());
            String[] keyValues = line.split("=");
            result.put(keyValues[0], keyValues[1]);
        });
        return result;
    }
}