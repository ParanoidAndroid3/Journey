package com.paranoidandroid.journey.wizard.models;

import java.util.Date;

/**
 * Created by epushkarskaya on 11/13/16.
 */

public class LegItem {

    private String destination;
    private Date startDate;
    private Date endDate;

    public LegItem(String destination, Date startDate, Date endDate) {
        this.destination = destination;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getDestination() {
        return destination;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }
}
