package com.paranoidandroid.journey.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import com.facebook.Profile;
import com.paranoidandroid.journey.R;
import com.paranoidandroid.journey.myjourneys.activities.MyJourneysActivity;
import com.paranoidandroid.journey.support.SharedPreferenceUtils;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.List;

import io.fabric.sdk.android.Fabric;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Add fabric crash reporting
        Fabric.with(this, new Crashlytics());

        if (ParseUser.getCurrentUser() != null) {
            // We are already signed in. Continue as current user.
            addFacebookLoginDataToSharedPreferences();
            navigateToNextActivity();
        } else {
            setContentView(R.layout.activity_login);
        }
    }

    public void onFacebookLogin(View view) {
        final List<String> permissions = Arrays.asList("email");

        ParseFacebookUtils.logInWithReadPermissionsInBackground(
                this, permissions, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    // TODO: crashlytics log
                    Log.e(TAG, "Failed to login: ", e);
                } else if (user == null) {
                    Log.i(TAG, "User cancelled Facebook Login.");
                } else {
                    // We have a logged in user, so let's get rid of this log in activity.
                    Log.i(TAG, "Facebook user logged in.");
                    addFacebookLoginDataToSharedPreferences();
                    navigateToNextActivity();
                }
            }
        });
    }

    private void addFacebookLoginDataToSharedPreferences() {
        Profile profile = Profile.getCurrentProfile();
        SharedPreferenceUtils.setCurrentUserName(this, profile.getName());
        SharedPreferenceUtils.setCurrentUserImageProfileUri(
                this, profile.getProfilePictureUri(200, 200).toString());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    private void navigateToNextActivity() {
        Intent intent = new Intent(this, MyJourneysActivity.class);
        startActivity(intent);
        finish();
    }
}
