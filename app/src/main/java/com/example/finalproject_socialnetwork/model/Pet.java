package com.example.finalproject_socialnetwork.model;

import java.time.LocalDate;
import java.time.Period;

public class Pet {
    public static final String GENDER_FEMALE = "GENDER_FEMALE";
    public static final String GENDER_MALE = "GENDER_MALE";

    public static final String KIND_DOG = "KIND_DOG";
    public static final String KIND_CAT = "KIND_CAT";

    private String id;
    private String petOwnerUid;
    private String name;
    private Date birthDate;
    private float weight;
    private String gender;
    private String kind;
    private String breed;

    // for firebase
    public Pet() {

    }

    public Pet(String id, String petOwnerUid, String name, Date birthDate,String kind ,float weight, String gender, String breed) {
        this.id = id;
        this.petOwnerUid = petOwnerUid;
        this.name = name;
        this.birthDate = birthDate;
        this.weight = weight;
        this.gender= gender;
        this.kind = kind;
        this.breed = breed;
    }

    public Pet(Pet pet ) {
        this(pet.getId(),pet.petOwnerUid, pet.getName() , pet.getBirthDate()
                , pet.getKind() , pet.getWeight() , pet.getGender() , pet.getBreed());

    }

    public String getName() {
        return name;
    }

    public String getBreed() {
        return breed;
    }

    public String getId() {
        return id;
    }

    public String getGender() {
        return gender;
    }

    public float getWeight() {
        return weight;
    }

    public String getKind() {
        return kind;
    }

    public String getPetOwnerUid() {
        return petOwnerUid;
    }

    public int calculateAge() {

        LocalDate localDate = LocalDate.of(birthDate.getYear(), birthDate.getMonth(), birthDate.getDay());
        LocalDate currentDate = LocalDate.now();

        if (birthDate != null && !localDate.isAfter(currentDate)) {
            return Period.between(localDate, currentDate).getYears();
        } else {
            return 0;
        }
    }

    public Date getBirthDate() {
        return birthDate;
    }

}
