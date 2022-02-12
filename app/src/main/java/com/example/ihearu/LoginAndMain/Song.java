package com.example.ihearu.LoginAndMain;

public class Song {

    private String key = "";
    private String name = "";
    private String genre="";

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

    public String getGenre() {
        return genre;
    }

    public Song setGenre(String genre) {
        this.genre = genre;
        return this;
    }

    @Override
    public String toString() {
        return "Song{" +
                "key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", genre='" + genre + '\'' +
                '}';
    }
}
