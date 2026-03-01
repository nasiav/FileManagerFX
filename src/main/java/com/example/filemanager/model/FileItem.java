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
    private List<List<String>> previousContents;

    public FileItem(String title, String author, String creationDate, int version, List<String> paragraphs, String category, List<List<String>> previousContents) {
        this.title = title;
        this.author = author;
        this.creationDate = creationDate;
        this.version = version;
        this.paragraphs = (paragraphs != null) ? paragraphs : new ArrayList<>();
        this.category = category;
        this.previousContents = previousContents;
    }

    // Getters
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getCreationDate() { return creationDate; }
    public int getVersion() { return version; }
    public List<String> getParagraphs() { return paragraphs; }
    public String getCategory() { return category; }
    public List<List<String>> getPreviousContents() {
        if (previousContents == null) previousContents = new ArrayList<>();
        return previousContents;
    }

    // Setters
    public void setParagraphs(List<String> paragraphs) { this.paragraphs = paragraphs; }
    public void setVersion(int version) { this.version = version; }
    public void setCategory(String category) { this.category = category; }
    public void setPreviousContents(List<List<String>> previousContents) {
        this.previousContents = (previousContents != null) ? previousContents : new ArrayList<>();
    }

}

