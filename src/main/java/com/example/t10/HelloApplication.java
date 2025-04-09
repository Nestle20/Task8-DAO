package com.example.t10;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelloApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Загружаем FXML-файл
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/t10/hello-view.fxml"));

        // Устанавливаем заголовок окна
        primaryStage.setTitle("Управление продуктами");

        // Создаем сцену и устанавливаем ее в окно
        primaryStage.setScene(new Scene(root, 600, 400));

        // Показываем окно
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}