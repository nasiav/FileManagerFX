package com.example.filemanager.model;

import java.util.ArrayList;
import java.util.List;

public class FileItem {
    private String title;
    private String author;
    private String creationDate;
    private int version;
    private List<String> paragraphs;
    private String category;

    public FileItem(String title, String author, String creationDate, int version, List<String> paragraphs, String category) {
        this.title = title;
        this.author = author;
        this.creationDate = creationDate;
        this.version = version;
        this.paragraphs = (paragraphs != null) ? paragraphs : new ArrayList<>();
        this.category = category;
    }

    // Getters
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getCreationDate() { return creationDate; }
    public int getVersion() { return version; }
    public List<String> getParagraphs() { return paragraphs; }
    public String getCategory() { return category; }

    // Setters
    public void setParagraphs(List<String> paragraphs) { this.paragraphs = paragraphs; }
    public void setVersion(int version) { this.version = version; }
}

