package com.example.filemanager;

import com.example.filemanager.model.User;
import com.example.filemanager.model.FileItem;
import com.example.filemanager.services.FileService;
import com.example.filemanager.services.CategoryService;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

public class Dashboard {

    private User user;
    private FileService fileService;
    private CategoryService categoryService;

    public Dashboard(User user) {
        this.user = user;
        this.fileService = new FileService();
        this.categoryService = new CategoryService();
    }

    public void start(Stage stage) {

        // --- Header ---
        Label welcome = new Label("Welcome, " + user.getUsername());
        Label role = new Label("Role: " + user.getRole());

        // --- TableView (left side) ---
        List<FileItem> accessibleFiles = fileService.getFilesByTitles(user.getAccessibleFiles());
        TableView<FileItem> table = new TableView<>(FXCollections.observableArrayList(accessibleFiles));

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

        // --- Preview / editor panel (right side) ---
        Label fTitle = new Label("Title:");
        TextField fileTitleField = new TextField();

        Label fCategory = new Label("Category:");
        ComboBox<String> categoryCombo = new ComboBox<>();
        categoryCombo.getItems().addAll(categoryService.getCategories());
        categoryCombo.setEditable(false); // Authors and Admins select only existing categories

        TextArea fileContentArea = new TextArea();
        fileContentArea.setWrapText(true);

        Button saveButton = new Button("Save");
        saveButton.setDisable(true); // initially disabled

        VBox editorBox = new VBox(8,
                fTitle, fileTitleField,
                fCategory, categoryCombo,
                new Label("Content:"), fileContentArea,
                saveButton
        );
        editorBox.setPadding(new Insets(10));

        // --- Buttons above table ---
        HBox tableButtons = new HBox(10);
        Button newFileButton = new Button("New File");
        newFileButton.setDisable(!(user.getRole().equalsIgnoreCase("author") ||
                                  user.getRole().equalsIgnoreCase("admin")));

        Button editCategoriesButton = new Button("Edit Categories");
        editCategoriesButton.setVisible(user.getRole().equalsIgnoreCase("admin"));

        tableButtons.getChildren().addAll(newFileButton, editCategoriesButton);

        // --- SplitPane ---
        SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(table, editorBox);
        splitPane.setDividerPositions(0.65);

        // --- Table selection: fill editor ---
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selected) -> {
            if (selected != null) {
                fileTitleField.setText(selected.getTitle());
                categoryCombo.getSelectionModel().select(selected.getCategory());
                fileContentArea.setText(String.join("\n\n", selected.getParagraphs()));

                // Enable editing only for admin or file author
                boolean canEdit = user.getRole().equalsIgnoreCase("admin") ||
                        selected.getAuthor().equals(user.getUsername());
                fileTitleField.setEditable(canEdit);
                categoryCombo.setDisable(!canEdit);
                fileContentArea.setEditable(canEdit);
                saveButton.setDisable(!canEdit);
            }
        });

        // --- New File button action ---
        newFileButton.setOnAction(e -> {
            fileTitleField.clear();
            fileContentArea.clear();
            if (!categoryService.getCategories().isEmpty()) {
                categoryCombo.getSelectionModel().selectFirst();
            }
            fileTitleField.setEditable(true);
            categoryCombo.setDisable(false);
            fileContentArea.setEditable(true);
            saveButton.setDisable(false);
            table.getSelectionModel().clearSelection();
        });

        // --- Save button action ---
        saveButton.setOnAction(e -> {
            String title = fileTitleField.getText().trim();
            String category = categoryCombo.getSelectionModel().getSelectedItem();
            List<String> paragraphs = List.of(fileContentArea.getText().split("\\n\\n"));

            if (title.isEmpty() || category == null || category.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Title and category cannot be empty.");
                alert.show();
                return;
            }

            FileItem selected = table.getSelectionModel().getSelectedItem();
            boolean isAuthorOrAdmin = user.getRole().equalsIgnoreCase("admin") ||
                    (selected != null && selected.getAuthor().equals(user.getUsername()));

            if (selected != null && isAuthorOrAdmin) {
                // Edit existing file
                FileItem updatedFile = new FileItem(
                        title,
                        selected.getAuthor(),
                        selected.getCreationDate(),
                        selected.getVersion() + 1,
                        paragraphs,
                        category
                );
                fileService.saveFile(updatedFile);
            } else if (selected == null) {
                // New file
                FileItem newFile = new FileItem(
                        title,
                        user.getUsername(),
                        LocalDate.now().toString(),
                        1,
                        paragraphs,
                        category
                );
                fileService.saveFile(newFile);
            }

            // Refresh table
            table.setItems(FXCollections.observableArrayList(fileService.getFilesByTitles(user.getAccessibleFiles())));
            saveButton.setDisable(true);
        });

        // --- Edit Categories button action ---
        editCategoriesButton.setOnAction(e -> {
            EditCategories editor = new EditCategories(categoryService);
            try {
                editor.start(new Stage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
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

        // --- Layout ---
        BorderPane root = new BorderPane();
        VBox topContent = new VBox(10, welcome, role, new Label("Your files:"), tableButtons, splitPane);
        topContent.setPadding(new Insets(10));

        root.setCenter(topContent);
        root.setBottom(logoutButton);
        BorderPane.setAlignment(logoutButton, Pos.BOTTOM_RIGHT);
        BorderPane.setMargin(logoutButton, new Insets(10));

        stage.setScene(new Scene(root, 1000, 500));
        stage.show();
    }
}
