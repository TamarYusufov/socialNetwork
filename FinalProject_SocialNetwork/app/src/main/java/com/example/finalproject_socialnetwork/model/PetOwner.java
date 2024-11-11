package com.example.finalproject_socialnetwork.model;

import java.util.ArrayList;
import java.util.List;

public class PetOwner extends User{
    // for firebase
    public PetOwner() {

    }

    public PetOwner(String uid, String name, String email, String address, String phone) {
        super(uid, User.USER_TYPE_PET_OWNER, name, email, address, phone);
    }

    public PetOwner(PetOwner petOwner) {
        this(petOwner.getUid(), petOwner.getName(), petOwner.getEmail(), petOwner.getAddress(),
                petOwner.getPhone());
    }
}
