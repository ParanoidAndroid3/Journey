package com.paranoidandroid.journey.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;

import java.util.Date;
import java.util.List;

/**
 * Object for representing the overall journey.
 */
@ParseClassName("Journey")
public class Journey extends ParseObject {

    // REST JSON Keys
    private static final String KEY_NAME = "name";
    private static final String KEY_CREATOR = "creator";
    private static final String KEY_COLLABORATORS = "collaborators";
    private static final String KEY_TRIP_TYPE = "type";
    private static final String KEY_TRIP_TAGS = "tags";
    private static final String KEY_LEGS = "legs";

    public Journey() {
        // Required default constructor.
    }

    public void setName(String name) {
        put(KEY_NAME, name);
    }

    public String getName() {
        return getString(KEY_NAME);
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

    public Date getStartDate() {
        if (getLegs().size() > 0) {
            return getLegs().get(0).getStartDate();
        }
        return null;
    }

    public Date getEndDate() {
        int legCount = getLegs().size();
        if (legCount > 0) {
            return getLegs().get(legCount - 1).getEndDate();
        }
        return null;
    }

    public static ParseQuery<Journey> createQuery() {
        ParseQuery<Journey> query = ParseQuery.getQuery(Journey.class);
        query.include("legs");
        query.include("legs.destination");
        query.include("legs.activities");
        query.include("legs.bookmarks");
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        return query;
    }
}
