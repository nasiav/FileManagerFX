package com.example.filemanager.services;

import com.example.filemanager.model.FileItem;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import java.io.FileWriter;
import java.io.Writer;
import java.util.Optional;


public class FileService {
    private List<FileItem> files;

    public FileService() {
        loadFiles();
    }

    private void loadFiles() {
        try (Reader reader = new InputStreamReader(
                getClass().getClassLoader().getResourceAsStream("data/files.json"), StandardCharsets.UTF_8)) {

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Type listType = new TypeToken<List<FileItem>>() {}.getType();
            files = gson.fromJson(reader, listType);

            if (files == null) files = new ArrayList<>();

        } catch (Exception e) {
            e.printStackTrace();
            files = new ArrayList<>();
        }
    }

    public List<FileItem> getFilesByTitles(List<String> titles) {
        List<FileItem> result = new ArrayList<>();
        for (FileItem f : files) {
            if (titles.contains(f.getTitle())) result.add(f);
        }
        return result;
    }
    //public void saveFile(FileItem updatedFile) {
        // Find index of the file with the same title
    //    Optional<FileItem> existing = files.stream()
    //            .filter(f -> f.getTitle().equals(updatedFile.getTitle()))
    //            .findFirst();

    //    if (existing.isPresent()) {
    //        int index = files.indexOf(existing.get());
    //        files.set(index, updatedFile); // replace with updated file
    //    } else {
    //        files.add(updatedFile); // optional: add if not existing
    //    }

    //    // Save back to JSON file
    //    try (Writer writer = new FileWriter("src/main/resources/data/files.json")) { 
    //        Gson gson = new GsonBuilder().setPrettyPrinting().create();  // <--- pretty printing
    //        gson.toJson(files, writer);
    //    } catch (Exception e) {
    //        e.printStackTrace();
    //    }
    //}
    public void saveFile(FileItem updatedFile) {

        Optional<FileItem> existing = files.stream()
                .filter(f -> f.getTitle().equals(updatedFile.getTitle()))
                .findFirst();

        if (existing.isPresent()) {
            FileItem oldFile = existing.get();

            List<List<String>> history = oldFile.getPreviousContents();
            history.add(0, new ArrayList<>(oldFile.getParagraphs()));

            if (history.size() > 2) {
                history = history.subList(0, 2);
            }

            updatedFile.setPreviousContents(history);

            updatedFile.setVersion(oldFile.getVersion() + 1);

            int index = files.indexOf(oldFile);
            files.set(index, updatedFile);

        } else {
            updatedFile.setVersion(1);
            updatedFile.setPreviousContents(new ArrayList<>());
            files.add(updatedFile);
        }

        try (Writer writer = new FileWriter("src/main/resources/data/files.json")) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(files, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<FileItem> getFiles() {
        return files;
    }
    public void deleteFile(FileItem file) {
        files.remove(file);

        try (Writer writer = new FileWriter("src/main/resources/data/files.json")) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(files, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}

