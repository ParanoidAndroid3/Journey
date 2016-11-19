package com.paranoidandroid.journey.models.ui;

public abstract class Recommendation {

    double lat;
    double lng;
    double rating;
    String address;
    String name;
    String id;

    public abstract String getImageURL();

    public String getId() { return id; }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public double getRating() {
        return rating;
    }

    public double getLatitude() {
        return lat;
    }

    public double getLongitude() {
        return lng;
    }
}
