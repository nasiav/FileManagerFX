package com.example.filemanager.model;

import java.util.ArrayList;
import java.util.Objects;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.filemanager.services.FileService;

/**
 * Represents a user of the file management system.
 * Contains identity information, role, accessible file categories,
 * and data about the latest file versions the user has seen.
 */
public class User {

    private String username;
    private String password;
    private String role;
    private List<String> accessibleCategories;
    private Map<String, Integer> lastSeenFileVersions;

    /**
     * Creates a new User instance.
     *
     * @param username the user's username
     * @param password the user's password
     * @param role the user's role (e.g. "admin", "user")
     * @param accessibleCategories the list of categories the user can access
     * @param lastSeenFileVersions a map of file identifiers to the last seen version
     */
    public User(String username, String password, String role,
                List<String> accessibleCategories,
                Map<String, Integer> lastSeenFileVersions) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.accessibleCategories = accessibleCategories;
        this.lastSeenFileVersions = lastSeenFileVersions;
    }

    /**
     * Returns the username of the user.
     *
     * @return the user's username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the password of the user.
     *
     * @return the user's password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Returns the role of the user.
     *
     * @return the user's role (e.g. "admin", "user")
     */
    public String getRole() {
        return role;
    }

    /**
     * Returns the list of categories the user can access.
     * If the list is null, it is lazily initialized.
     *
     * @return a list of accessible categories
     */
    public List<String> getAccessibleCategories() {
        if (accessibleCategories == null) {
            accessibleCategories = new ArrayList<>();
        }
        return accessibleCategories;
    }

    /**
     * Returns the list of files visible to this user.
     * Admin users can see all files. Non-admin users can see
     * files they authored or files belonging to categories
     * they have access to.
     *
     * @param fileService the service providing access to all files
     * @return a list of files visible to the user
     */
    public List<FileItem> getVisibleFiles(FileService fileService) {
        boolean isAdmin = this.getRole().equalsIgnoreCase("admin");

        if (isAdmin) {
            return new ArrayList<>(fileService.getFiles());
        }

        List<String> categories = this.getAccessibleCategories();
        String username = this.getUsername();

        return fileService.getFiles().stream()
                .filter(file ->
                        Objects.equals(file.getAuthor(), username) ||
                        (file.getCategory() != null && categories.contains(file.getCategory()))
                )
                .toList();
    }

    /**
     * Returns a map containing the last seen version for each file.
     * If the map is null, it is lazily initialized.
     *
     * @return a map of file identifiers to last seen versions
     */
    public Map<String, Integer> getLastSeenFileVersions() {
        if (lastSeenFileVersions == null) {
            lastSeenFileVersions = new HashMap<>();
        }
        return lastSeenFileVersions;
    }

    /**
     * Sets the categories the user can access.
     * If the provided list is null, an empty list is used instead.
     *
     * @param accessibleCategories the new accessible categories
     */
    public void setAccessibleCategories(List<String> accessibleCategories) {
        this.accessibleCategories = (accessibleCategories != null)
                ? accessibleCategories
                : new ArrayList<>();
    }

    /**
     * Sets the map containing the last seen file versions.
     * If the provided map is null, an empty map is used instead.
     *
     * @param snapshot the new map of last seen file versions
     */
    public void setLastSeenFileVersions(Map<String, Integer> snapshot) {
        this.lastSeenFileVersions = (snapshot != null) ? snapshot : new HashMap<>();
    }
}