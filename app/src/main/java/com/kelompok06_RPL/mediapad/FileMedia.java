package com.kelompok06_RPL.mediapad;

import android.os.Parcel;
import android.os.Parcelable;

public class FileMedia implements Parcelable {
    private String id, title, displayName, size, path, date,duration;

    public FileMedia(String id, String title, String displayName, String size, String path, String date, String duration) {
        this.id = id;
        this.title = title;
        this.displayName = displayName;
        this.size = size;
        this.path = path;
        this.date = date;
        this.duration = duration;
    }

    protected FileMedia(Parcel in) {
        id = in.readString();
        title = in.readString();
        displayName = in.readString();
        size = in.readString();
        path = in.readString();
        date = in.readString();
        duration = in.readString();
    }

    public static final Creator<FileMedia> CREATOR = new Creator<FileMedia>() {
        @Override
        public FileMedia createFromParcel(Parcel in) {
            return new FileMedia(in);
        }

        @Override
        public FileMedia[] newArray(int size) {
            return new FileMedia[size];
        }
    };

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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(title);
        parcel.writeString(displayName);
        parcel.writeString(size);
        parcel.writeString(path);
        parcel.writeString(date);
        parcel.writeString(duration);
    }
}
