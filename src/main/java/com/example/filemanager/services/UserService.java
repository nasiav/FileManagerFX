package com.example.filemanager.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.example.filemanager.model.FileItem;
import com.example.filemanager.model.User;

import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserService {

    private List<User> users;

    public UserService() {
        users = loadUsers(); // make sure the users field is initialized
    }

    public List<User> loadUsers() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data/users.json")) {

            if (inputStream == null) {
                throw new RuntimeException("users.json not found in resources/data!");
            }

            try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                User[] userArray = gson.fromJson(reader, User[].class);

                List<User> userList = new ArrayList<>();
                for (User u : userArray) {
                    // Initialize categories list if null
                    if (u.getAccessibleCategories() == null) {
                        u.setAccessibleCategories(new ArrayList<>());
                    }
                    userList.add(u);
                }

                return userList;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void saveUsers(List<User> users) {
        try (Writer writer = new FileWriter("src/main/resources/data/users.json")) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(users, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public User authenticate(String username, String password) {
        return users.stream()
                .filter(u -> u.getUsername().equals(username) &&
                             u.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }

    public void snapshotVisibleFiles(User user, FileService fileService) {
        List<FileItem> visibleFiles = user.getVisibleFiles(fileService);

        // store title -> version
        Map<String, Integer> snapshot = new HashMap<>();
        for (FileItem f : visibleFiles) {
            snapshot.put(f.getTitle(), f.getVersion());
        }

        user.setLastSeenFileVersions(snapshot);
    }
    
    public List<String> getUpdatedFiles(User user, FileService fileService) {
        List<FileItem> currentVisible = user.getVisibleFiles(fileService);
        Map<String, Integer> lastSnapshot = user.getLastSeenFileVersions();

        List<String> changedFiles = new ArrayList<>();

        for (FileItem f : currentVisible) {
            Integer oldVersion = lastSnapshot.get(f.getTitle());

            if (oldVersion == null) {
                changedFiles.add(f.getTitle() + " (new file)");
            } else if (f.getVersion() > oldVersion) {
                changedFiles.add(f.getTitle() + " (updated)");
            }
        }

        for (String oldTitle : lastSnapshot.keySet()) {
            boolean stillExists = currentVisible.stream()
                    .anyMatch(f -> f.getTitle().equals(oldTitle));

            if (!stillExists) {
                changedFiles.add(oldTitle + " (deleted)");
            }
        }

        return changedFiles;
    }
}
