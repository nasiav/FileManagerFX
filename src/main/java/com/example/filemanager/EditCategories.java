package com.example.filemanager;

import com.example.filemanager.services.CategoryService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class EditCategories {

    private CategoryService categoryService;

    public EditCategories(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    public void start(Stage stage) {
        stage.setTitle("Edit Categories");

        ListView<String> listView = new ListView<>();
        listView.setItems(FXCollections.observableArrayList(categoryService.getCategories()));

        TextField newCategoryField = new TextField();
        newCategoryField.setPromptText("New category");

        Button addButton = new Button("Add");
        addButton.setOnAction(e -> {
            String cat = newCategoryField.getText().trim();
            if (!cat.isEmpty() && !categoryService.getCategories().contains(cat)) {
                categoryService.addCategory(cat);
                listView.setItems(FXCollections.observableArrayList(categoryService.getCategories()));
                newCategoryField.clear();
            }
        });

        Button removeButton = new Button("Remove Selected");
        removeButton.setOnAction(e -> {
            String selected = listView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                categoryService.removeCategory(selected);
                listView.setItems(FXCollections.observableArrayList(categoryService.getCategories()));
            }
        });

        VBox layout = new VBox(10, listView, newCategoryField, addButton, removeButton);
        layout.setPadding(new Insets(10));

        stage.setScene(new Scene(layout, 300, 400));
        stage.show();
    }
}
