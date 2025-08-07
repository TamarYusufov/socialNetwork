package com.example.finalproject_socialnetwork.model;

public class FoundPetReport {
    private String id;
    private String uid;
    private MapLocation mapLocation;
    private String description;
    private long timestamp;

    // for firebase
    public FoundPetReport() {

    }

    public FoundPetReport(String id, String uid, MapLocation mapLocation, String description, long timestamp) {
        this.id = id;
        this.uid = uid;
        this.mapLocation = mapLocation;
        this.description = description;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public String getUid() {
        return uid;
    }

    public MapLocation getMapLocation() {
        return mapLocation;
    }

    public String getDescription() {
        return description;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
