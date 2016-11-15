package com.paranoidandroid.journey.wizard.models;

/**
 * Created by epushkarskaya on 11/13/16.
 */

public class AutoCompleteItem {

    String city;
    String country;
    long id;

    public AutoCompleteItem(String city, String country, long id) {
        this.city = city;
        this.country = country;
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public long getId() {
        return id;
    }

}
