package com.kelompok06_RPL.mediapad;

public class IconModel {
    private int imageView;
    private String ic_title;

    public IconModel(int imageView, String ic_title) {
        this.imageView = imageView;
        this.ic_title = ic_title;
    }

    public int getImageView() {
        return imageView;
    }

    public void setImageView(int imageView) {
        this.imageView = imageView;
    }

    public String getIc_title() {
        return ic_title;
    }

    public void setIc_title(String ic_title) {
        this.ic_title = ic_title;
    }
}
