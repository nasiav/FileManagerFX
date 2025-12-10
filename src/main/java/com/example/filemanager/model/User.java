package com.example.filemanager.model;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String username;
    private String password;
    private String role;
    private List<String> accessibleFiles = new ArrayList<>();

    public User(String username, String password, String role, List<String> accessibleFiles) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.accessibleFiles = (accessibleFiles != null) ? accessibleFiles : new ArrayList<>();
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public List<String> getAccessibleFiles() {
        if (accessibleFiles == null) {
            accessibleFiles = new ArrayList<>();
        }
        return accessibleFiles;
    }
    public void setAccessibleFiles(List<String> accessibleFiles) {
        this.accessibleFiles = (accessibleFiles != null) ? accessibleFiles : new ArrayList<>();
    }


}