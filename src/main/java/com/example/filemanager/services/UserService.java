package com.example.filemanager.services;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.example.filemanager.model.User;

import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;

public class UserService {

    private List<User> users;

    public UserService() {
        loadUsers();
    }

    private void loadUsers() {
        try (Reader reader = new InputStreamReader(
                getClass().getResourceAsStream("/data/users.json")
        )) {
            Type listType = new TypeToken<List<User>>(){}.getType();
            users = new Gson().fromJson(reader, listType);
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
}
