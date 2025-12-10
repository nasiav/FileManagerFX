package com.example.filemanager.services;

import com.example.filemanager.model.FileItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FileService {
    private List<FileItem> files;

    public FileService() {
        loadFiles();
    }

    private void loadFiles() {
        try (Reader reader = new InputStreamReader(
                getClass().getClassLoader().getResourceAsStream("data/files.json"), StandardCharsets.UTF_8)) {

            Gson gson = new Gson();
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
}

