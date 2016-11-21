package com.paranoidandroid.journey.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.Date;
import java.util.List;

/**
 * One leg of a journey.
 */
@ParseClassName("Leg")
public class Leg extends ParseObject {
    private static final String KEY_START_DATE = "startDate";
    private static final String KEY_END_DATE = "endDate";
    private static final String KEY_DESTINATION = "destination";
    private static final String KEY_ACTIVITIES = "activities";

    public Leg() {
    }

    public void setLegDuration(Date startDate, Date endDate) {
        if (startDate.after(endDate)) {
            throw new IllegalArgumentException(
                    "startDate must be the same or earlier than endDate.");
        }
        put(KEY_START_DATE, startDate);
        put(KEY_END_DATE, endDate);
    }

    public Date getStartDate() {
        return getDate(KEY_START_DATE);
    }

    public Date getEndDate() {
        return getDate(KEY_END_DATE);
    }

    public void setDestination(Destination destination) {
        put(KEY_DESTINATION, destination);
    }

    public Destination getDestination() {
        return (Destination) getParseObject(KEY_DESTINATION);
    }

    public void addActivity(Activity activity) {
        add(KEY_ACTIVITIES, activity);
    }

    public void removeActivity(Activity activity) {
        // TODO: test me!!!
        List<Activity> activities = getActivities();
        if (activities.remove(activity)) {
            put(KEY_ACTIVITIES, activities);
        }
    }

    public List<Activity> getActivities() {
        return getList(KEY_ACTIVITIES);
    }
}
