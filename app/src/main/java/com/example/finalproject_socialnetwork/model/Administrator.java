package com.example.finalproject_socialnetwork.model;

public class Administrator extends User {

    // for firebase
    public Administrator() {

    }

    public Administrator(String uid, String name, String email, String address, String phone) {
        super(uid, User.USER_TYPE_ADMINISTRATOR, name, email, address, phone);
    }
}
