package com.example.filemanager;

import com.example.filemanager.model.User;
import com.example.filemanager.model.FileItem;
import com.example.filemanager.services.FileService;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

public class Dashboard {

    private User user;
    private FileService fileService;

    public Dashboard(User user) {
        this.user = user;
        this.fileService = new FileService();
    }

    public void start(Stage stage) {

        Label welcome = new Label("Welcome, " + user.getUsername());
        Label role = new Label("Role: " + user.getRole());

        // Get only the files the user has access to
        List<FileItem> accessibleFiles = fileService.getFilesByTitles(user.getAccessibleFiles());

        // --- TableView setup ---
        TableView<FileItem> table = new TableView<>();
        table.setItems(FXCollections.observableArrayList(accessibleFiles));

        TableColumn<FileItem, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleCol.setPrefWidth(150);

        TableColumn<FileItem, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        categoryCol.setPrefWidth(100);

        TableColumn<FileItem, String> authorCol = new TableColumn<>("Author");
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        authorCol.setPrefWidth(100);

        TableColumn<FileItem, String> versionCol = new TableColumn<>("Version");
        versionCol.setCellValueFactory(new PropertyValueFactory<>("version"));
        versionCol.setPrefWidth(80);

        TableColumn<FileItem, String> createdCol = new TableColumn<>("Created");
        createdCol.setCellValueFactory(new PropertyValueFactory<>("creationDate"));
        createdCol.setPrefWidth(100);


        table.getColumns().addAll(titleCol, categoryCol, authorCol, versionCol, createdCol);
        
        // Make rows clickable
        table.setRowFactory(tv -> {
            TableRow<FileItem> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 1) {
                    FileItem clickedFile = row.getItem();
                    System.out.println("Clicked on: " + clickedFile.getTitle());
                    // TODO: you can open file, show details, etc.
                }
            });
            return row;
        });

        // --- Logout button ---
        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> {
            Main mainApp = new Main();
            try {
                mainApp.start(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        BorderPane root = new BorderPane();
        VBox content = new VBox(10, welcome, role, new Label("Your files:"), table);
        content.setPadding(new Insets(10));

        root.setCenter(content);
        root.setBottom(logoutButton);
        BorderPane.setAlignment(logoutButton, Pos.BOTTOM_RIGHT);
        BorderPane.setMargin(logoutButton, new Insets(10));

        stage.setScene(new Scene(root, 650, 400));
        stage.show();
    }
}
