package com.example.filemanager;

import com.example.filemanager.model.User;
import com.example.filemanager.services.UserService;

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
        usernameField.setMaxWidth(200);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(200);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        Button loginButton = new Button("Login");

        // --- Login logic ---
        loginButton.setOnAction(e -> {
            String user = usernameField.getText();
            String pass = passwordField.getText();

            UserService userService = new UserService();
            User u = userService.authenticate(user, pass);

            if (u == null) {
                errorLabel.setText("Invalid username or password!");
            } else {
                // CHANGED CODE: stage.setScene(createPostLoginScene(stage, u.getUsername(), u.getRole()));
                openDashboard(stage, u);
            }
        });

        layout.getChildren().addAll(usernameField, passwordField, loginButton, errorLabel);

        return new Scene(layout, 300, 200);
    }

    private void openDashboard(Stage stage, User user) {
        Dashboard dashboard = new Dashboard(user);
        try {
            dashboard.start(stage);  
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
