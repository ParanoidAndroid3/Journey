package com.paranoidandroid.journey.models;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Day {
    int series;
    Date date;
    Leg leg;

    public Day(int series, Date date, Leg leg) {
        this.series = series;
        this.date = date;
        this.leg = leg;
    }

    public String getCity() {
        return this.leg.getDestination().getCityName();
    }

    public Date getDate() { return this.date; }

    public Leg getLeg() {
        return this.leg;
    }

    public String getFormattedDate() {
        SimpleDateFormat format = new SimpleDateFormat("M/d");
        return format.format(this.date);
    }

    public String getSeriesString() {
        return "DAY " + this.series;
    }
}
