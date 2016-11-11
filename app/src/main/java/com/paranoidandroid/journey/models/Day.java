package com.paranoidandroid.journey.models;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Day {
    int series;
    Date date;
    String city;

    public Day(int series, Date date, String city) {
        this.series = series;
        this.date = date;
        this.city = city;
    }

    public String getCity() {
        return this.city;
    }

    public String getFormattedDate() {
        SimpleDateFormat format = new SimpleDateFormat("M/d");
        return format.format(this.date);
    }

    public String getSeriesString() {
        return "DAY " + this.series;
    }
}
