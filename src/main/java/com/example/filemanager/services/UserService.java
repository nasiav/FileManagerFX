package com.example.filemanager.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.example.filemanager.model.User;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

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
                    if (u.getAccessibleFiles() == null) {
                        u.setAccessibleFiles(new ArrayList<>());
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

    public User authenticate(String username, String password) {
        return users.stream()
                .filter(u -> u.getUsername().equals(username) &&
                             u.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }
}
