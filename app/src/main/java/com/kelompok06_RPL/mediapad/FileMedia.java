package com.kelompok06_RPL.mediapad;

public class FileMedia {
    private int id, title, displayName, size, path, date,duration;

    public FileMedia(int id, int title, int displayName, int size, int path, int date, int duration) {
        this.id = id;
        this.title = title;
        this.displayName = displayName;
        this.size = size;
        this.path = path;
        this.date = date;
        this.duration = duration;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTitle() {
        return title;
    }

    public void setTitle(int title) {
        this.title = title;
    }

    public int getDisplayName() {
        return displayName;
    }

    public void setDisplayName(int displayName) {
        this.displayName = displayName;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getPath() {
        return path;
    }

    public void setPath(int path) {
        this.path = path;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
