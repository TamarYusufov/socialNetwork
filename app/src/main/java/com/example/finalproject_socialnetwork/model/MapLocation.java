package com.example.finalproject_socialnetwork.model;

import com.google.android.gms.maps.model.LatLng;

public class MapLocation {
    private double latitude;
    private double longitude;

    // for firebase
    public MapLocation() {
    }

    public MapLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public  static MapLocation fromLatLng(LatLng latLng) {
        return new MapLocation(latLng.latitude, latLng.longitude);
    }
    public LatLng toLatLng() {
        return new LatLng(latitude, longitude);
    }
}
