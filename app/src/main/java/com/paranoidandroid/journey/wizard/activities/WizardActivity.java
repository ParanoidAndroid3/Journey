package com.paranoidandroid.journey.wizard.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.paranoidandroid.journey.R;
import com.paranoidandroid.journey.legplanner.activities.PlannerActivity;
import com.paranoidandroid.journey.models.Journey;
import com.paranoidandroid.journey.wizard.adapters.WizardPagerAdapter;
import com.paranoidandroid.journey.wizard.utils.JourneyBuilder;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;


/**
 * Created by epushkarskaya on 11/13/16.
 */

public class WizardActivity extends BaseWizardActivity implements View.OnClickListener {

    private static final String TAG = "WizardActivity";

    private WizardPagerAdapter pagerAdapter;
    private ViewPager viewpager;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wizard);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        enableFab(false);
        fab.setOnClickListener(this);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        pagerAdapter = new WizardPagerAdapter(getSupportFragmentManager());
        viewpager = (ViewPager) findViewById(R.id.viewpager);
        viewpager.setAdapter(pagerAdapter);
    }

    /**
     * Navigates to the next fragment and updates the page count in the
     * pagerAdapter if necessary.
     */
    private void goToNextFragment(int currentFragment) {
        int numFragments = pagerAdapter.getCount();

        if (currentFragment == 0) {
            if (numFragments == 1) {
                pagerAdapter.incrementCount();
            }
            pagerAdapter.notifyDataSetChanged();
            viewpager.setCurrentItem(1, true);
        } else if (currentFragment == 1) {
            if (numFragments == 2) {
                pagerAdapter.incrementCount();
            }
            pagerAdapter.notifyDataSetChanged();
            viewpager.setCurrentItem(2, true);
            fab.setImageResource(R.drawable.ic_check);
        } else {
            Log.e(TAG, "Cannot go to next fragment");
        }
    }

    /**
     * Returns true if all necessary data is present
     */
    private boolean readyToPublish() {
        return nameComplete() && legsComplete() && tagsComplete();
    }

    /*
     * Either navigates to the next fragment or creates a new Journey in parse,
     * depending on current fragment.
     */
    @Override
    public void onClick(View view) {
        int currentFragment = viewpager.getCurrentItem();
        getSupportFragmentManager();

        if (currentFragment < 2) {
            goToNextFragment(currentFragment);
        } else if (readyToPublish()) {

            showProgressBar();

            final Journey journey = JourneyBuilder.buildJourney(
                    ParseUser.getCurrentUser(), journeyData);
            journey.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        // Saved successfully.
                        Log.d(TAG, "Journed saved!");

                        String journeyId = journey.getObjectId();
                        Intent intent = new Intent(getApplicationContext(), PlannerActivity.class);
                        intent.putExtra("journey_id", journeyId);
                        startActivity(intent);
                        hideProgressBar();
                    } else {
                        // The save failed.
                        Log.e(TAG, "Error saving Journey: " + e);
                    }
                }
            });

        } else {
            Toast.makeText(this, "Missing data. Please fill out form", Toast.LENGTH_LONG).show();
        }

    }

}
