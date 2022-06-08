package com.kelompok06_RPL.mediapad;

public class VideoFiIes {
    private String id, title, displayName,size,path,dateAdd;

    public VideoFiIes(String id, String title, String displayName, String size, String path, String dateAdd) {
        this.id = id;
        this.title = title;
        this.displayName = displayName;
        this.size = size;
        this.path = path;
        this.dateAdd = dateAdd;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDateAdd() {
        return dateAdd;
    }

    public void setDateAdd(String dateAdd) {
        this.dateAdd = dateAdd;
    }
}
