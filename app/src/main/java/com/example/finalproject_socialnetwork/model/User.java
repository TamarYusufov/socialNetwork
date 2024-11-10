package com.example.finalproject_socialnetwork.model;

import java.io.Serializable;

public abstract class User {
    public static final String USER_TYPE_PET_OWNER = "PET_OWNER";
    public static final String USER_TYPE_PROFESSIONAL = "PROFESSIONAL";
    public static final String USER_TYPE_ADMINISTRATOR = "ADMINISTRATOR";
    private String uid;
    private String type;
    private String name;
    private String email;
    private String address;
    private String phone;

    // for firebase
    public User() {

    }

    public User(String uid, String type, String name, String email, String address,
                String phone) {
        this.uid = uid;
        this.type = type;
        this.name = name;
        this.email = email;
        this.address = address;
        this.phone = phone;
    }


    public String getUid() {
        return uid;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}


