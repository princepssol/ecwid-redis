package com.ecwid.app.redis.config;

import com.ecwid.app.App;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class RedisConfig {
    private static final Properties properties;
    private static final String KEY_URL = "url";
    private static final String KEY_PORT = "port";
    private static final String KEY_MASTER = "master.name";
    private static final String PROPERTIES_PATH = "redis-connection.properties";

    public static final String URL;
    public static final int PORT;
    public static final String MASTER_NAME;

    static {
        properties = initProperties();
        URL = getUrl();
        PORT = getPort();
        MASTER_NAME = getMasterName();
    }

    private static Properties initProperties() {
        Properties properties = new Properties();
        InputStream inputStream = App.class.getClassLoader().getResourceAsStream(PROPERTIES_PATH);
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            System.err.println("Can't read properties from file");
            System.exit(0);
        }
        return properties;
    }

    private static String getUrl() {
        return properties.getProperty(KEY_URL);
    }

    private static int getPort() {
        return Integer.parseInt(properties.getProperty(KEY_PORT));
    }

    private static String getMasterName() {
        return properties.getProperty(KEY_MASTER);
    }
}
