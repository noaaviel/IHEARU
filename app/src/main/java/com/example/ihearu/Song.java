package com.example.ihearu;

import android.media.Image;

public class Song {

    private String key = "";
    private String name = "";
    private Image image;

    public Song() { }

    public String getKey() {
        return key;
    }

    public Song setKey(String key) {
        this.key = key;
        return this;
    }

    public String getName() {
        return name;
    }

    public Song setName(String name) {
        this.name = name;
        return this;
    }

    public Image getImage() {
        return image;
    }

    public Song setImage(Image image) {
        this.image = image;
        return this;
    }
}
