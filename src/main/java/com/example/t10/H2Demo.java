package com.example.t10;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class H2Demo {
    public static void main(String[] args) {
        // URL для подключения к файловой БД (хранится в домашней папке)
        String url = "jdbc:h2:~/test"; // Будет создан файл ~/myh2db.mv.db
        String user = "sa"; // Имя пользователя
        String password = "";

        try {
            // 1. Автоматическая регистрация драйвера (не требуется в JDBC 4.0+)
            // 2. Подключение к БД
            Connection connection = DriverManager.getConnection(url);

            System.out.println("Подключение к H2 успешно!");

            // 3. Пример создания таблицы
            connection.createStatement().execute(
                    "CREATE TABLE IF NOT EXISTS users (" +
                            "id INT PRIMARY KEY, " +
                            "name VARCHAR(50))"
            );

            // 4. Закрытие соединения (обязательно!)
            connection.close();
        } catch (SQLException e) {
            System.err.println("Ошибка подключения: " + e.getMessage());
        }
    }
}