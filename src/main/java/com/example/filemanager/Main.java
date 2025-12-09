package com.example.filemanager;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        Scene loginScene = createLoginScene(stage);
        stage.setScene(loginScene);
        stage.setTitle("Login");
        stage.show();
    }
    private Scene createLoginScene(Stage stage) {

        // --- Layout ---
        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.setPrefWidth(300);
        layout.setPrefHeight(200);

        // --- Fields ---
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        Button loginButton = new Button("Login");

        // --- Login logic ---
        loginButton.setOnAction(e -> {
            String user = usernameField.getText();
            String pass = passwordField.getText();

            if (user.equals("admin") && pass.equals("admin123")) {
                System.out.println("Login OK!");

                // for now, just replace the scene with a blank one
                stage.setScene(new Scene(new Label("Welcome!"), 400, 300));
            } else {
                errorLabel.setText("Invalid username or password!");
            }
        });

        layout.getChildren().addAll(usernameField, passwordField, loginButton, errorLabel);

        return new Scene(layout, 300, 200);
    }


    public static void main(String[] args) {
        launch();
    }
}
