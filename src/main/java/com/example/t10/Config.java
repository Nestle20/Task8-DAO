package com.example.t10;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Класс для работы с конфигурацией приложения
 */
public final class Config {
    private static final String CONFIG_FILE = "application.properties";
    private static final Properties properties = new Properties();

    // Статический блок инициализации
    static {
        loadProperties();
    }

    private Config() {
        // Запрещаем создание экземпляров класса
    }

    /**
     * Загружает конфигурационные параметры из файла
     */
    private static void loadProperties() {
        // 1. Попытка загрузки из classpath (для JAR)
        try (InputStream input = Config.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input != null) {
                properties.load(input);
                return;
            }
        } catch (IOException ex) {
            System.err.println("Ошибка чтения конфигурации из classpath: " + ex.getMessage());
        }

        // 2. Попытка загрузки из внешнего файла (для разработки)
        Path externalConfig = Paths.get("config", CONFIG_FILE);
        if (Files.exists(externalConfig)) {
            try (InputStream input = Files.newInputStream(externalConfig)) {
                properties.load(input);
            } catch (IOException ex) {
                System.err.println("Ошибка чтения внешнего конфига: " + ex.getMessage());
            }
        } else {
            System.err.println("Конфигурационный файл не найден: " + externalConfig);
        }
    }

    /**
     * Получает значение параметра или null если не найден
     */
    public static String get(String key) {
        return properties.getProperty(key);
    }

    /**
     * Получает значение параметра или значение по умолчанию
     */
    public static String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    // Специфичные методы для удобства

    public static String getDbUrl() {
        return get("db.url", "jdbc:h2:~/productdb;DB_CLOSE_DELAY=-1");
    }

    public static String getDbUser() {
        return get("db.user", "sa");
    }

    public static String getDbPassword() {
        return get("db.password", "");
    }

    public static String getInMemoryDbUrl() {
        return get("db.memory.url", "jdbc:h2:mem:productdb;DB_CLOSE_DELAY=-1");
    }

    public static String getCsvFilePath() {
        return get("csv.file.path", "products.csv");
    }

    public static boolean isDebugMode() {
        return Boolean.parseBoolean(get("debug.mode", "false"));
    }
}