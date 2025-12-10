package com.example.filemanager;

import com.example.filemanager.model.User;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class Dashboard {

    private User user;

    public Dashboard(User user) {
        this.user = user;
    }

    public void start(Stage stage) {

        Label welcome = new Label("Welcome, " + user.getUsername());
        Label role = new Label("Role: " + user.getRole());

        ListView<String> fileList = new ListView<>(
            FXCollections.observableArrayList(user.getAccessibleFiles())
        );

        // --- Logout button ---
        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> {
            // Go back to login screen
            Main mainApp = new Main();
            try {
                mainApp.start(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        BorderPane root = new BorderPane();
        VBox content = new VBox(10, welcome, role, new Label("Your files:"), fileList);
        content.setPadding(new Insets(10));

        root.setCenter(content);
        root.setBottom(logoutButton);
        BorderPane.setAlignment(logoutButton, Pos.BOTTOM_RIGHT);
        BorderPane.setMargin(logoutButton, new Insets(10));

        stage.setScene(new Scene(root, 500, 350));
        stage.show();
    }
}
