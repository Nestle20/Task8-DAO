package com.example.t10;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class Config {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = Config.class.getResourceAsStream("/application.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException e) {
            System.err.println("Error loading config: " + e.getMessage());
        }
    }

    public static String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public static String getCsvFilePath() {
        return get("csv.file.path", "products.csv");
    }

    public static String getDbUrl() {
        return get("db.url", "jdbc:h2:~/productdb;DB_CLOSE_DELAY=-1");
    }
}