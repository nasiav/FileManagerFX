package com.example.filemanager;

import com.example.filemanager.model.User;
import com.example.filemanager.model.FileItem;
import com.example.filemanager.services.FileService;
import com.example.filemanager.services.UserService;
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
import java.util.ArrayList;
import java.util.List;

public class Dashboard {

    private User user;
    private FileService fileService;
    private CategoryService categoryService;
    private UserService userService;

    private void resetEditorFields(TextField titleField, ComboBox<String> categoryCombo, TextArea contentArea, Button saveButton) {
        titleField.clear();
        contentArea.clear();
        if (!categoryCombo.getItems().isEmpty()) {
            categoryCombo.getSelectionModel().selectFirst();
        } else {
            categoryCombo.getSelectionModel().clearSelection();
        }
        saveButton.setDisable(true);
    }
    
    public Dashboard(User user) {
        this.user = user;
        this.fileService = new FileService();
        this.categoryService = new CategoryService();
        this.userService = new UserService();
    }

    public void start(Stage stage) {


        // --- File updates! ---
        List<String> updates = userService.getUpdatedFiles(user, fileService);

        if (!updates.isEmpty()) {
            String message = "Files have changed since your last login:\n" + String.join("\n", updates);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
            alert.setTitle("File Updates");
            alert.setHeaderText("Attention!");
            alert.show();
        }

        // Optional: immediately update snapshot so notifications aren’t repeated
        userService.snapshotVisibleFiles(user, fileService);

        // Save to JSON
        List<User> allUsers = userService.loadUsers();
        for (int i = 0; i < allUsers.size(); i++) {
            if (allUsers.get(i).getUsername().equals(user.getUsername())) {
                allUsers.set(i, user);
                break;
            }
        }
        userService.saveUsers(allUsers);

        // --- Header ---
        int totalCategories = categoryService.getCategories().size();
        int totalFilesInSystem = fileService.getFiles().size();
        int totalFilesForUser = user.getVisibleFiles(fileService).size();

        Label welcome = new Label("Welcome, " + user.getUsername());
        Label role = new Label("Role: " + user.getRole());
        String categoriesText = String.join(", ", user.getAccessibleCategories());
        Label categoriesLabel = new Label("Your assigned reading categories: " + categoriesText);
        Label totalCategoriesLabel = new Label("Total categories in system: " + totalCategories);
        Label totalFilesLabel = new Label("Total files in system: " + totalFilesInSystem);
        Label userFilesLabel = new Label("Files assigned to you: " + totalFilesForUser);

        // --- TableView (left side) ---
        List<FileItem> accessibleFiles = user.getVisibleFiles(fileService);
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

        //editorBox.setPadding(new Insets(10));
        Button saveButton = new Button("Save");
        saveButton.setDisable(true);

        Button historyButton = new Button("Previous Versions");
        historyButton.setDisable(true);

        Button deleteButton = new Button("Delete");
        deleteButton.setDisable(true);

        HBox editorButtons = new HBox(10, saveButton, historyButton, deleteButton);

        VBox editorBox = new VBox(8,
                fTitle, fileTitleField,
                fCategory, categoryCombo,
                new Label("Content:"), fileContentArea,
                editorButtons
        );

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

                boolean canEdit = user.getRole().equalsIgnoreCase("admin") || selected.getAuthor().equals(user.getUsername());

                fileTitleField.setEditable(canEdit);
                categoryCombo.setDisable(!canEdit);
                fileContentArea.setEditable(!canEdit ? false : true);
                saveButton.setDisable(!canEdit);
                deleteButton.setDisable(!canEdit);

                boolean hasHistory = selected.getPreviousContents() != null & !selected.getPreviousContents().isEmpty();

                historyButton.setDisable(!(canEdit && hasHistory));
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

            boolean isAdmin = user.getRole().equalsIgnoreCase("admin");
            boolean isAuthor = selected != null && selected.getAuthor().equals(user.getUsername());
            boolean isAuthorOrAdmin = isAdmin || isAuthor;

            if (selected != null && isAuthorOrAdmin) {
                // Edit existing file: increment version & store previous content
                List<List<String>> previousContents = new ArrayList<>(selected.getPreviousContents() != null ? selected.getPreviousContents() : new ArrayList<>());
                previousContents.add(selected.getParagraphs()); // add current content before editing
                if (previousContents.size() > 2) previousContents.remove(0); // keep only last 2 versions

                FileItem updatedFile = new FileItem(
                        title,
                        selected.getAuthor(),
                        selected.getCreationDate(),
                        selected.getVersion() + 1,
                        paragraphs,
                        category,
                        previousContents
                );
                fileService.saveFile(updatedFile);
            } else if (selected == null) {
                // New file: initialize empty previousContents
                FileItem newFile = new FileItem(
                        title,
                        user.getUsername(),
                        LocalDate.now().toString(),
                        1,
                        paragraphs,
                        category,
                        new ArrayList<>()
                );
                fileService.saveFile(newFile);
            }

            //refresh table and file preview
            table.setItems(FXCollections.observableArrayList(user.getVisibleFiles(fileService)));
            resetEditorFields(fileTitleField, categoryCombo, fileContentArea, saveButton);
            saveButton.setDisable(true);
        });

        // -- Previous versions button action --
        historyButton.setOnAction(e -> {
            FileItem selected = table.getSelectionModel().getSelectedItem();

            if (selected == null) {
                new Alert(Alert.AlertType.WARNING, "Select a file first.").show();
                return;
            }

            boolean canEdit = user.getRole().equalsIgnoreCase("admin") ||
                    user.getUsername().equals(selected.getAuthor());

            if (!canEdit) {
                new Alert(Alert.AlertType.ERROR, "You don't have permission to view previous versions.").show();
                return;
            }

            List<List<String>> previous = selected.getPreviousContents();

            if (previous == null || previous.isEmpty()) {
                new Alert(Alert.AlertType.INFORMATION, "No previous versions available.").show();
                return;
            }

            StringBuilder sb = new StringBuilder("Previous versions:\n\n");

            for (int i = 0; i < previous.size(); i++) {
                sb.append("Version -").append(i + 1).append(":\n");
                sb.append(String.join("\n\n", previous.get(i)));
                sb.append("\n-----------------------------\n");
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Previous Versions");
            alert.setHeaderText("Last 2 versions of this file");
            alert.setContentText(sb.toString());
            alert.getDialogPane().setPrefWidth(500);
            alert.show();
        });

        //-- Delete File button --
        deleteButton.setOnAction(e -> {
            FileItem selected = table.getSelectionModel().getSelectedItem();
            if (selected != null && (user.getRole().equalsIgnoreCase("admin") ||
                                    selected.getAuthor().equals(user.getUsername()))) {
                fileService.deleteFile(selected);
                //refresh table and file preview
                table.setItems(FXCollections.observableArrayList(user.getVisibleFiles(fileService)));
                resetEditorFields(fileTitleField, categoryCombo, fileContentArea, saveButton);
            }
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
            userService.snapshotVisibleFiles(user, fileService);
            //List<User> allUsers = userService.loadUsers();

            for (int i = 0; i < allUsers.size(); i++) {
                if (allUsers.get(i).getUsername().equals(user.getUsername())) {
                    allUsers.set(i, user); 
                    break;
                }
            }

            userService.saveUsers(allUsers);

            Main mainApp = new Main();
            try {
                mainApp.start(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // --- Layout ---
        BorderPane root = new BorderPane();
        VBox topContent = new VBox(
                10,
                welcome,
                role,
                categoriesLabel,
                totalCategoriesLabel,
                totalFilesLabel,
                userFilesLabel,
                new Label("Your files:"),
                tableButtons,
                splitPane
        );
        topContent.setPadding(new Insets(10));

        root.setCenter(topContent);
        root.setBottom(logoutButton);
        BorderPane.setAlignment(logoutButton, Pos.BOTTOM_RIGHT);
        BorderPane.setMargin(logoutButton, new Insets(10));

        stage.setScene(new Scene(root, 1000, 500));
        stage.show();


    }
}
