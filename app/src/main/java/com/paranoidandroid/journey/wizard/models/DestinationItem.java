package com.paranoidandroid.journey.wizard.models;

/**
 * Created by epushkarskaya on 11/13/16.
 */

public class DestinationItem {

    String city;
    String country;
    long latitude;
    long longitude;

    public DestinationItem(String city, String country, long latitude, long longitude) {
        this.city = city;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public long getLatitude() {
        return latitude;
    }

    public long getLongitude() {
        return longitude;
    }
}
