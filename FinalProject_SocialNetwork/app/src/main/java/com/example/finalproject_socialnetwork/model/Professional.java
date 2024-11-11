package com.example.finalproject_socialnetwork.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Professional extends User {
    public static String SPECIALITY_VETERINARY = "SPECIALITY_VETERINARY";
    public static String SPECIALITY_GROOMING = "SPECIALITY_GROOMING";
    public static String SPECIALITY_TRAINING = "SPECIALITY_TRAINING";
    public static String SPECIALITY_BOARDING = "SPECIALITY_BOARDING";
    public static String SPECIALITY_PET_STORE = "SPECIALITY_PET_STORE";

    private static final Map<String, String> specialityToDecoratedMap = new HashMap<String, String>() {
        {
            put(SPECIALITY_VETERINARY, "Veterinarian");
            put(SPECIALITY_GROOMING, "Grooming");
            put(SPECIALITY_TRAINING, "Training");
            put(SPECIALITY_BOARDING, "Boarding Kennel");
            put(SPECIALITY_PET_STORE, "Pet Store");
        }
    };

    private static final Map<String, String> decortedToSpecialityMap = new HashMap<>();

    static {
        for(String speciality: specialityToDecoratedMap.keySet()) {
            decortedToSpecialityMap.put(specialityToDecoratedMap.get(speciality), speciality);
        }


        int a =5;
    }


    private String speciality;
    private boolean approved;
    //certifications ???

    // for firebase
    public Professional() {

    }

    public Professional(String uid, String name, String email, String address,
                        String phone, String decortedSpeciality) {
        super(uid, User.USER_TYPE_PROFESSIONAL, name, email, address, phone);
        this.speciality = decortedToSpecialityMap.get(decortedSpeciality);
        this.approved = false;
    }

    public Professional(Professional professional) {
        this(professional.getUid(), professional.getName(), professional.getEmail(), professional.getAddress(),
                professional.getPhone(), professional.getSpeciality());

    }


    public String getSpeciality() {
        return speciality;
    }

    public String decoratedSpeciality() {
        return specialityToDecoratedMap.get(speciality);
    }

    public boolean isApproved() {
        return approved;
    }
}
