package com.example.t10;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("hello-view.fxml")));
        stage.setTitle("Product Management System");
        stage.setScene(new Scene(root, 800, 600));
        stage.setOnCloseRequest(e -> {
            try {
                DBConnect dbConnect = new DBConnect();
                if (dbConnect.getConnection() != null) {
                    dbConnect.disconnect();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
