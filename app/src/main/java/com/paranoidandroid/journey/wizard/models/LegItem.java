package com.paranoidandroid.journey.wizard.models;

import java.util.Calendar;

/**
 * Created by epushkarskaya on 11/13/16.
 */

public class LegItem {

    private String destination;
    private String placesId;
    private Calendar startDate;
    private Calendar endDate;

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getPlacesId() {
        return placesId;
    }

    public void setPlacesId(String placesId) {
        this.placesId = placesId;
    }

    public Calendar getStartDate() {
        return startDate;
    }

    public void setStartDate(Calendar startDate) {
        this.startDate = startDate;
    }

    public Calendar getEndDate() {
        return endDate;
    }

    public void setEndDate(Calendar endDate) {
        this.endDate = endDate;
    }

}
