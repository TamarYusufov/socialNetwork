package com.example.finalproject_socialnetwork.model;

public class Category {
    private String name;
    private int imageResourceId;

    public Category(String name, int imageResource) {
        this.name = name;
        this.imageResourceId = imageResource;
    }

    public String getName() {
        return name;
    }

    public int getImageResource() {
        return imageResourceId;
    }

}
