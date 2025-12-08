package com.example.filemanager;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        Label label = new Label("Hello world!");
        Scene scene = new Scene(label, 250, 50);
        stage.setScene(scene);
        stage.setTitle("announcement");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
