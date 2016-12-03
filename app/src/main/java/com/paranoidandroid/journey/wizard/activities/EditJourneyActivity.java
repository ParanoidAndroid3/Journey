package com.paranoidandroid.journey.wizard.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.paranoidandroid.journey.R;
import com.paranoidandroid.journey.wizard.fragments.LegsFragment;
import com.paranoidandroid.journey.wizard.fragments.NameFragment;
import com.paranoidandroid.journey.wizard.fragments.TagsFragment;
import com.paranoidandroid.journey.wizard.fragments.WizardFragment;
import com.paranoidandroid.journey.wizard.utils.JourneyBuilder;
import com.parse.ParseException;
import com.parse.SaveCallback;

import java.util.Map;

/**
 * Created by epushkarskaya on 11/27/16.
 */

public class EditJourneyActivity extends BaseWizardActivity implements View.OnClickListener, WizardFragment.LoadingListener {

    private static final String TAG = "EditJourneyActivity";

    public static final int EDIT_MODE_TITLE = 0;
    public static final int EDIT_MODE_LEGS = 1;
    public static final int EDIT_MODE_TAGS = 2;

    private static final String EXTRA_JOURNEY_ID = "com.paranoidandroid.journey.JOURNEY_ID";
    private static final String EXTRA_EDIT_MODE = "com.paranoidandroid.journey.EDIT_MODE";

    private String journeyId;
    private int editMode;
    private boolean changed;
    private ContentLoadingProgressBar progressBar;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_journey);

        progressBar = (ContentLoadingProgressBar) findViewById(R.id.pbEditJourney);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_check);
        fab.setOnClickListener(this);

        changed = false;

        Bundle extras = getIntent().getExtras();

        editMode = extras.getInt(EXTRA_EDIT_MODE, -1);
        journeyId = extras.getString(EXTRA_JOURNEY_ID, null);

        if (journeyId != null && editMode != -1) {
            addFragment();
        } else {
            Log.e(TAG, "Could not find journey id for editing");
        }
    }

    @Override
    public void updateJourneyData(Map<String, Object> data) {
        changed = true;
        super.updateJourneyData(data);
    }

    private void addFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        WizardFragment fragment = null;
        switch (editMode) {
            case EDIT_MODE_TITLE:
                fragment = NameFragment.newInstance(journeyId);
                break;
            case EDIT_MODE_LEGS:
                fragment = LegsFragment.newInstance(journeyId);
                break;
            case EDIT_MODE_TAGS:
                fragment = TagsFragment.newInstance(journeyId);
                break;
        }

        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
    }

    @Override
    public void onClick(View view) {

        showProgressBar();

        if (!changed) {
            finish();
        } else {

            boolean success = false;

            switch (editMode) {
                case EDIT_MODE_TITLE:
                    if (success = nameComplete()) {
                        JourneyBuilder.setName(journey, journeyData);
                    }
                    break;
                case EDIT_MODE_LEGS:
                    success = legsComplete();
                    break;
                case EDIT_MODE_TAGS:
                    if (success = tagsComplete()) {
                        JourneyBuilder.setTags(journey, journeyData);
                    }
                    break;
            }

            if (success) {
                journey.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            finish();
                            //hideProgressBar();
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                hideProgressBar();
                Toast.makeText(this, "Missing data. Please fill out form", Toast.LENGTH_LONG).show();
            }
        }
    }

    public static Intent createEditIntent(Context context, String journeyId, int editMode) {
        Intent intent = new Intent(context, EditJourneyActivity.class);
        intent.putExtra(EXTRA_JOURNEY_ID, journeyId);
        intent.putExtra(EXTRA_EDIT_MODE, editMode);
        return intent;
    }

    // ------ WizardFragment.LoadingListener implementation ------- //

    @Override
    public void hideLoading() {
        progressBar.hide();
        findViewById(R.id.rlProgress).setVisibility(View.GONE);
    }

    @Override
    public void showLoading() {
        findViewById(R.id.rlProgress).setVisibility(View.VISIBLE);
        progressBar.show();
    }
}
