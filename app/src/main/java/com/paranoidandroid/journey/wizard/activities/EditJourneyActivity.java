package com.paranoidandroid.journey.wizard.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.paranoidandroid.journey.R;
import com.paranoidandroid.journey.wizard.fragments.LegsFragment;
import com.paranoidandroid.journey.wizard.fragments.NameFragment;
import com.paranoidandroid.journey.wizard.fragments.TagsFragment;
import com.paranoidandroid.journey.wizard.fragments.WizardFragment;
import com.paranoidandroid.journey.wizard.utils.JourneyBuilder;

import java.util.Map;

/**
 * Created by epushkarskaya on 11/27/16.
 */

public class EditJourneyActivity extends BaseWizardActivity implements View.OnClickListener {

    private static final String TAG = "EditJourneyActivity";

    public static final int EDIT_MODE_TITLE = 0;
    public static final int EDIT_MODE_LEGS = 1;
    public static final int EDIT_MODE_TAGS = 2;

    private static final String EXTRA_JOURNEY_ID = "com.paranoidandroid.journey.JOURNEY_ID";
    private static final String EXTRA_EDIT_MODE = "com.paranoidandroid.journey.EDIT_MODE";

    private String journeyId;
    private int editMode;
    private boolean changed;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_journey);

        fab = (FloatingActionButton) findViewById(R.id.fab);
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
        boolean success = false;
        switch (editMode) {
            case EDIT_MODE_TITLE:
                success = handleNameChange();
                break;
            case EDIT_MODE_LEGS:
                success = handleLegsChange();
                break;
            case EDIT_MODE_TAGS:
                success = handleTagsChange();
                break;
        }

        if (success) {
            finish();
        } else {
            Toast.makeText(this, "Missing data. Please fill out form", Toast.LENGTH_LONG).show();
        }
    }

    private boolean handleNameChange() {
        if (!changed) {
            return true;
        }

        if (!nameComplete()) {
            return false;
        }

        JourneyBuilder.setName(journey, journeyData);
        journey.saveInBackground();

        return true;
    }

    private boolean handleLegsChange() {
        if (!changed) {
            return true;
        }

        if (!legsComplete()) {
            return false;
        }

        journey.saveInBackground();

        return true;
    }

    private boolean handleTagsChange() {
        if (!changed) {
            return true;
        }

        if (!tagsComplete()) {
            return false;
        }

        JourneyBuilder.setTags(journey, journeyData);
        journey.saveInBackground();

        return true;
    }

    public static Intent createEditIntent(Context context, String journeyId, int editMode) {
        Intent intent = new Intent(context, EditJourneyActivity.class);
        intent.putExtra(EXTRA_JOURNEY_ID, journeyId);
        intent.putExtra(EXTRA_EDIT_MODE, editMode);
        return intent;
    }

}
