package ru.ramprox.server.factory.reader;

import ru.ramprox.server.factory.Utils;

import java.net.URL;
import java.util.Map;

public class UserDefinedConfigFileReader implements ConfigReader {

    public URL fileLocation;

    public UserDefinedConfigFileReader(URL url) {
        this.fileLocation = url;
    }

    @Override
    public Map<String, String> read() {
        return Utils.readFromFile(fileLocation);
    }
}