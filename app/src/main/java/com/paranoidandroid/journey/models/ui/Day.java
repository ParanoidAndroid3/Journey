package com.paranoidandroid.journey.models.ui;

import com.paranoidandroid.journey.models.Leg;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Day {
    int series;
    Date date;
    Leg leg;
    int legOrder;

    public Day(int series, Date date, Leg leg, int legOrder) {
        this.series = series;
        this.date = date;
        this.leg = leg;
        this.legOrder = legOrder;
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

    public int getSeries() { return this.series; }

    public int getLegOrder() { return this.legOrder; }
}
