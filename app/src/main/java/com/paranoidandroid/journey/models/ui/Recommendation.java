package com.paranoidandroid.journey.models.ui;

import org.parceler.Parcel;

@Parcel
public class Recommendation {

    double lat;
    double lng;
    double rating;
    String address;
    String name;
    String id;
    String imageUrl;

    boolean added;
    boolean bookmarked;

    public boolean isAdded() {
        return added;
    }

    public boolean isBookmarked() {
        return bookmarked;
    }

    public void setBookmarked(boolean bookmarked) {
        this.bookmarked = bookmarked;
    }

    public void setAdded(boolean added) {
        this.added = added;
    }

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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
