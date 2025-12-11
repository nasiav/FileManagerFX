package com.example.filemanager.services;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CategoryService {

    private List<String> categories;
    private final String filePath = "src/main/resources/data/categories.json";

    public CategoryService() {
        loadCategories();
    }

    private void loadCategories() {
        try (Reader reader = new InputStreamReader(
                new FileInputStream(filePath), StandardCharsets.UTF_8)) {

            Gson gson = new Gson();
            Type listType = new TypeToken<List<String>>() {}.getType();
            categories = gson.fromJson(reader, listType);

            if (categories == null) categories = new ArrayList<>();

        } catch (FileNotFoundException e) {
            // File does not exist yet, start with empty list
            categories = new ArrayList<>();
            saveCategories(); // create the file
        } catch (Exception e) {
            e.printStackTrace();
            categories = new ArrayList<>();
        }
    }

    public List<String> getCategories() {
        return categories;
    }

    public void addCategory(String newCategory) {
        if (!categories.contains(newCategory)) {
            categories.add(newCategory);
            saveCategories();
        }
    }

    private void saveCategories() {
        try (Writer writer = new FileWriter(filePath, StandardCharsets.UTF_8)) {
            new Gson().toJson(categories, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void removeCategory(String category) {
        if (categories.contains(category)) {
            categories.remove(category);
            saveCategories();
        }
    }

}
