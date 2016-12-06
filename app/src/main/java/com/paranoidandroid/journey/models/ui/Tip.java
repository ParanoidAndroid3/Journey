package com.paranoidandroid.journey.models.ui;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Parcel
public class Tip {
    Date createdAt;
    String text;
    String userName;
    String userPhotoUrl;

    public String getUserPhotoUrl() {
        return userPhotoUrl;
    }

    public String getUserName() {
        return userName;
    }

    public String getText() {
        return text;
    }

    public Date getCreatedAt() {
        return createdAt;
    }


    public static List<Tip> parseJSON(JSONArray array) {
        List<Tip> list = new ArrayList<>();
        try {
            for (int i = 0; i < array.length(); i++) {
                list.add(parseTip(array.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

    private static Tip parseTip(JSONObject tipObject) {
        Tip tip = new Tip();
        try {
            tip.createdAt = new Date(tipObject.getLong("createdAt") * 1000);
            tip.text = tipObject.getString("text");
            JSONObject user = tipObject.getJSONObject("user");
            tip.userName = user.getString("firstName") + " " + (user.isNull("lastName") ? "": user.getString("lastName"));
            JSONObject photo = user.getJSONObject("photo");
            tip.userPhotoUrl = photo.getString("prefix") + "cap200" + photo.getString("suffix");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return tip;
    }
}
