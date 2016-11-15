package com.paranoidandroid.journey.wizard.models;

import java.util.Date;

/**
 * Created by epushkarskaya on 11/13/16.
 */

public class LegItem {

    private String city;
    private String country;
    private long id;
    private Date startDate;
    private Date endDate;
    private boolean visible;

    public LegItem() {
        visible = false;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
