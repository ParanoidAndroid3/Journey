package com.paranoidandroid.journey.activityviewer.activities;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.elmargomez.typer.Font;
import com.elmargomez.typer.Typer;
import com.paranoidandroid.journey.R;
import com.paranoidandroid.journey.databinding.ActivityActivityDetailsBinding;
import com.paranoidandroid.journey.models.Activity;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

/**
 * Show details for a Journey activity.
 */
public class ActivityDetailsActivity extends AppCompatActivity {
    private static final String TAG = "ActivityDetailsActivity";
    private static final String EXTRA_ACTIVITY_ID =
            "com.paranoidandroid.journey.EXTRA_ACTIVITY_ID";

    private ActivityActivityDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_activity_details);
        setupToolbar();
        getSupportActionBar().setTitle("");
        acceptArguments(getIntent().getExtras());
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        Typeface font = Typer.set(this).getFont(Font.ROBOTO_MEDIUM);
        binding.collapsingToolbar.setExpandedTitleTypeface(font);
    }

    private void acceptArguments(Bundle extras) {
        if (extras != null) {
            String activityId = extras.getString(EXTRA_ACTIVITY_ID);
            if (activityId != null) {
                loadData(activityId);
            }
        }
    }

    private void loadData(final String activityId) {
        // TODO: Show spinner.

        ParseQuery<Activity> query = ParseQuery.getQuery(Activity.class);
        query.getInBackground(activityId, new GetCallback<Activity>() {
            @Override
            public void done(Activity object, ParseException e) {
                // TODO: hide spinner.

                if (e == null) {
                    showActivity(object);
                } else {
                    Log.e(TAG, "Unable to load activity(" + activityId + ")");
                }
            }
        });
    }

    public void showActivity(Activity activity) {
        getSupportActionBar().setTitle(activity.getTitle());

        String imageUrl = activity.getImageUrl();
        Glide.with(this).load(imageUrl).into(binding.ivBackdrop);
    }

    public static Intent createIntent(Context context, String activityId) {
        Intent intent = new Intent(context, ActivityDetailsActivity.class);
        intent.putExtra(EXTRA_ACTIVITY_ID, activityId);
        return intent;
    }
}
