package com.paranoidandroid.journey.wizard.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.Toast;

import com.paranoidandroid.journey.R;
import com.paranoidandroid.journey.models.Journey;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import static com.loopj.android.http.AsyncHttpClient.log;


/**
 * Created by epushkarskaya on 11/27/16.
 */

public class EditJourneyActivity extends BaseWizardActivity implements View.OnClickListener {

    private static final String TAG = "EditJourneyActivity";

    public static final int EDIT_MODE_TITLE = 0;
    public static final int EDIT_MODE_LEGS = 1;
    public static final int EDIT_MODE_TAGS = 2;

    public static final String EXTRA_JOURNEY_ID = "com.paranoidandroid.journey.JOURNEY_ID";
    public static final String EXTRA_EDIT_MODE = "com.paranoidandroid.journey.EDIT_MODE";

    private Journey mJourney;
    private int editMode;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_journey);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setClickable(false); // todo: remove?
        fab.setOnClickListener(this);

        Bundle extras = getIntent().getExtras();
        String journeyId = null;

        if (extras != null) {
            editMode = extras.getInt(EXTRA_EDIT_MODE);
            journeyId = extras.getString(EXTRA_JOURNEY_ID, null);
        }

        if (journeyId != null) {
            fetchJourney(journeyId);
            addFragment();
        } else {
            log.e(TAG, "Could not find journey id for editing");
        }
    }

    private void fetchJourney(String journeyId)  {
        if (journeyId.isEmpty()) {
            return;
        }
        ParseQuery<Journey> query = Journey.createQuery();
        query.getInBackground(journeyId, new GetCallback<Journey>() {
            public void done(final Journey journey, ParseException e) {
                if (e == null) {
                    mJourney = journey;
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void addFragment() {
        // todo: populate screen with fragment
    }

    @Override
    public void onClick(View view) {
        boolean success = false;
        switch (editMode) {
            case EDIT_MODE_TITLE:
                success = nameComplete();
                break;
            case EDIT_MODE_LEGS:
                success = legsComplete();
                break;
            case EDIT_MODE_TAGS:
                success = tagsComplete();
                break;
        }

        if (success) {
            // todo: save & end activity
        } else {
            Toast.makeText(this, "Missing data. Please fill out form", Toast.LENGTH_LONG).show();
        }

    }

    public static Intent createEditIntent(Context context, String journeyId, int editMode) {
        Intent intent = new Intent(context, WizardActivity.class);
        intent.putExtra(EXTRA_JOURNEY_ID, journeyId);
        intent.putExtra(EXTRA_EDIT_MODE, editMode);
        return intent;
    }

}
