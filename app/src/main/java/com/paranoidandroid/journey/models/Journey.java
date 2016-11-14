package com.paranoidandroid.journey.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseRelation;

import java.util.List;

/**
 * Object for representing the overall journey.
 */
@ParseClassName("Journey")
public class Journey extends ParseObject {

    // REST JSON Keys
    private static final String KEY_TITLE = "title";
    private static final String KEY_CREATOR = "creator";
    private static final String KEY_COLLABORATORS = "collaborators";
    private static final String KEY_TRIP_TYPE = "type";
    private static final String KEY_TRIP_TAGS = "tags";
    private static final String KEY_LEGS = "legs";

    public Journey() {
        // Required default constructor.
    }

    public void setTitle(String title) {
        put(KEY_TITLE, title);
    }

    public String getTitle() {
        return getString(KEY_TITLE);
    }

    public void setCreator(User creator) {
        put(KEY_CREATOR, creator);
    }

    public User getCreator() {
        return (User) getParseUser(KEY_CREATOR);
    }

    public void addCollaborator(User collaborator) {
        getCollaboratorsRelation().add(collaborator);
        saveInBackground();
    }

    public void removeCollaborator(User collaborator) {
        getCollaboratorsRelation().remove(collaborator);
        saveInBackground();
    }

    public ParseRelation<User> getCollaboratorsRelation() {
        return getRelation(KEY_COLLABORATORS);
    }

    public void setTripType(String type) {
        put(KEY_TRIP_TYPE, type);
    }

    public String getTripType() {
        return getString(KEY_TRIP_TYPE);
    }

    public void addTag(Tag tag) {
        getTagsRelation().add(tag);
        saveInBackground();
    }

    public void removeTag(Tag tag) {
        getTagsRelation().remove(tag);
        saveInBackground();
    }

    public ParseRelation<Tag> getTagsRelation() {
        return getRelation(KEY_TRIP_TAGS);
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
