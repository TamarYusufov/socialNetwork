package com.example.finalproject_socialnetwork.model;

public class Chat {
    private String id;
    private String petOwnerUid;
    private String professionalUid;
    private String petId;

    // for firebase
    public Chat() {
    }

    public Chat(String id, String petOwnerUid, String professionalUid, String petId) {
        this.id = id;
        this.petOwnerUid = petOwnerUid;
        this.professionalUid = professionalUid;
        this.petId = petId;
    }

    public String getId() {
        return id;
    }

    public String getPetOwnerUid() {
        return petOwnerUid;
    }

    public String getProfessionalUid() {
        return professionalUid;
    }

    public String getPetId() {
        return petId;
    }
}
