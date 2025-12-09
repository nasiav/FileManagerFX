package com.example.filemanager;

import com.example.filemanager.model.User;
import com.example.filemanager.services.UserService;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
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
                // Switch to post-login scene
                stage.setScene(createPostLoginScene(stage, u.getUsername(), u.getRole()));
            }
        });

        layout.getChildren().addAll(usernameField, passwordField, loginButton, errorLabel);

        return new Scene(layout, 300, 200);
    }
    private Scene createPostLoginScene(Stage stage, String username, String role) {

        BorderPane postLoginLayout = new BorderPane();
        postLoginLayout.setPadding(new Insets(10));

        // Center: Welcome message
        Label welcomeLabel = new Label("Welcome, " + username + "! Your role is: " + role.toLowerCase() +".");
        postLoginLayout.setCenter(welcomeLabel);

        // Bottom-right: Logout button
        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> {
            stage.setScene(createLoginScene(stage)); // go back to login
        });

        BorderPane.setAlignment(logoutButton, Pos.BOTTOM_RIGHT);
        postLoginLayout.setBottom(logoutButton);

        return new Scene(postLoginLayout, 400, 300);
    }



    public static void main(String[] args) {
        launch();
    }
}
