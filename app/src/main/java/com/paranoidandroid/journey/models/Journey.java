package com.paranoidandroid.journey.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.List;

/**
 * Object for representing the overall journey.
 */
@ParseClassName("Journey")
public class Journey extends ParseObject {

    // REST JSON Keys
    private static final String KEY_CREATOR = "creator";
    private static final String KEY_COLLABORATORS = "collaborators";
    private static final String KEY_TRIP_TYPE = "type";
    private static final String KEY_TRIP_TAGS = "tags";
    private static final String KEY_LEGS = "legs";

    public Journey() {
        // Required default constructor.
    }

    public void setCreator(User creator) {
        put(KEY_CREATOR, creator);
    }

    public User getCreator() {
        return (User) getParseUser(KEY_CREATOR);
    }

    public void addCollaborator(User collaborator) {
        add(KEY_COLLABORATORS, collaborator);
    }

    public List<User> getCollaborators() {
        return getList(KEY_COLLABORATORS);
    }

    public void setTripType(String type) {
        put(KEY_TRIP_TYPE, type);
    }

    public String getTripType() {
        return getString(KEY_TRIP_TYPE);
    }

    public void addTag(String tag) {
        add(KEY_TRIP_TAGS, tag);
    }

    public List<String> getTags() {
        return getList(KEY_TRIP_TAGS);
    }

    public void addLeg(Leg leg) {
        add(KEY_LEGS, leg);
    }

    public List<Leg> getLegs() {
        return getList(KEY_LEGS);
    }

    public void removeLeg(Leg leg) {
        // TODO: test me!!! Equals probably doesn't work as expected.
        List<Leg> legs = getLegs();
        if (legs.remove(leg)) {
            put(KEY_LEGS, legs);
        }
    }
}
