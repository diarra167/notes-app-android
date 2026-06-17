package com.example.notesapp.model;

public class Note {
    private int id;
    private String title;
    private String content;
    private String color;
    private boolean isFavorite;
    private String date;

    public Note() {}

    public Note(int id, String title, String content, String color, boolean isFavorite, String date) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.color = color;
        this.isFavorite = isFavorite;
        this.date = date;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}