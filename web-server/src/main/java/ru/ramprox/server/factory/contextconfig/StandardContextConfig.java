package ru.ramprox.server.factory.contextconfig;

import ru.ramprox.server.factory.environment.Environment;
import ru.ramprox.server.factory.environment.StandardEnvironmentImpl;
import ru.ramprox.server.factory.reader.CommandLineArgsReader;
import ru.ramprox.server.factory.reader.ConfigReader;
import ru.ramprox.server.factory.reader.DefaultConfigFileReader;
import ru.ramprox.server.factory.reader.UserDefinedConfigFileReader;
import ru.ramprox.server.factory.Factory;
import ru.ramprox.server.factory.context.ApplicationContext;
import ru.ramprox.server.factory.context.Context;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

public class StandardContextConfig implements ContextConfig {

    public Context getContext(Environment environment) {
        Context context = new ApplicationContext(environment);
        Factory factory = getFactory(context);
        context.setFactory(factory);
        return context;
    }

    public Context getContext(String... args) {
        return getContext(getEnvironment(args));
    }

    protected Factory getFactory(Context context) {
        return new Factory(context);
    }

    protected Environment getEnvironment(String... args) {
        List<ConfigReader> readers = getReaders(args);
        return new StandardEnvironmentImpl(readers);
    }

    protected List<ConfigReader> getReaders(String... args) {
        List<ConfigReader> result = new LinkedList<>();
        result.add(getDefaultConfigFileReader());
        if(args != null && args.length > 0) {
            result.add(getCommandLineArgsReader(args));
        }
        return result;
    }

    protected ConfigReader getDefaultConfigFileReader() {
        return new DefaultConfigFileReader();
    }

    protected ConfigReader getCommandLineArgsReader(String... args) {
        return new CommandLineArgsReader(args) {
            @Override
            public ConfigReader getUserDefinedConfigFileReader(String userConfigFileLocation) {
                return StandardContextConfig.this.getUserDefinedConfigFileReader(userConfigFileLocation);
            }
        };
    }

    protected ConfigReader getUserDefinedConfigFileReader(String fileLocation) {
        try {
            return new UserDefinedConfigFileReader(new URL(fileLocation));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}