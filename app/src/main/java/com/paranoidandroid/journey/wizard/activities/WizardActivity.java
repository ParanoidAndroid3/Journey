package com.paranoidandroid.journey.wizard.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.paranoidandroid.journey.R;
import com.paranoidandroid.journey.legplanner.activities.PlannerActivity;
import com.paranoidandroid.journey.models.Journey;
import com.paranoidandroid.journey.wizard.adapters.WizardPagerAdapter;
import com.paranoidandroid.journey.wizard.fragments.WizardFragment;
import com.paranoidandroid.journey.wizard.models.LegItem;
import com.paranoidandroid.journey.wizard.utils.JourneyBuilder;
import com.parse.ParseException;
import com.parse.SaveCallback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by epushkarskaya on 11/13/16.
 */

public class WizardActivity extends AppCompatActivity implements WizardFragment.OnItemUpdatedListener, View.OnClickListener {

    private static final String TAG = "WizardActivity";

    /**
     * This map will collected all of the data needed to create a new Journey.
     **/
    private Map<String, Object> journeyData;

    private WizardPagerAdapter pagerAdapter;
    private ViewPager viewpager;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wizard);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setClickable(false);
        fab.setOnClickListener(this);

        pagerAdapter = new WizardPagerAdapter(getSupportFragmentManager());
        viewpager = (ViewPager) findViewById(R.id.viewpager);
        viewpager.setAdapter(pagerAdapter);

        journeyData = new HashMap<>();
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

    public void onWizardExit(View view) {
        finish();
    }

    /**
     * Returns true if all necessary data is present
     */
    private boolean readyToPublish() {
        return nameComplete() && legsComplete() && tagsComplete();
    }

    private boolean nameComplete() {
        if (!journeyData.containsKey(JourneyBuilder.NAME_KEY)){
            return false;
        }
        return !((String) journeyData.get(JourneyBuilder.NAME_KEY)).isEmpty();
    }

    private boolean legsComplete() {
        if (!journeyData.containsKey(JourneyBuilder.LEGS_KEY)){
            return false;
        }
        List<LegItem> legs = (List<LegItem>) journeyData.get(JourneyBuilder.LEGS_KEY);
        for (LegItem leg : legs) {
            if (leg.getDestination().isEmpty()) {
                return false;
            }
            if (leg.getPlacesId().isEmpty()) {
                return false;
            }
            if (leg.getStartDate() == null) {
                return false;
            }
            if (leg.getEndDate() == null) {
                return false;
            }
        }
        return true;
    }

    private boolean tagsComplete() {
        if (!journeyData.containsKey(JourneyBuilder.SIZE_KEY)){
            return false;
        }
        return !((String) journeyData.get(JourneyBuilder.SIZE_KEY)).isEmpty();
    }

    /**
     * Necessary to implement View.OnClickListener.
     *
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
            final Journey journey = JourneyBuilder.buildJourney(journeyData);
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

    /**
     * Necessary to implement WizardFragment.OnItemUpdatedListener.
     */
    @Override
    public void updateJourneyData(Map<String, Object> data) {
        journeyData.putAll(data);
    }

    /**
     * Necessary to implement WizardFragment.OnItemUpdatedListener.
     */
    @Override
    public void enableFab(boolean enable) {
        fab.setClickable(enable);
    }
}
