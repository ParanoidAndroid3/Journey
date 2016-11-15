package com.paranoidandroid.journey.wizard.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.paranoidandroid.journey.myjourneys.activities.MyJourneysActivity;
import com.paranoidandroid.journey.R;
import com.paranoidandroid.journey.wizard.adapters.WizardPagerAdapter;
import com.paranoidandroid.journey.wizard.fragments.WizardFragment;
import com.paranoidandroid.journey.wizard.utils.JourneyBuilderUtils;

import java.util.HashMap;
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
     * Iterates through all fragments to see if all necessarydata to create a new
     * Journey have been entered.
     */
    private boolean allFragmentsComplete() {
        for (int i = 0; i < pagerAdapter.getCount(); i++) {
            WizardFragment fragment = (WizardFragment) pagerAdapter.getItem(i);
            if (!fragment.readyToPublish()) {
                return false;
            }
        }
        return true;
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

        if (currentFragment < 2) {
            goToNextFragment(currentFragment);
        } else if (allFragmentsComplete()) {
            int journeyId = JourneyBuilderUtils.buildJourney(journeyData);

            //todo: navigate to map view activity instead of MyJourneysActivity
            Intent intent = new Intent(getApplicationContext(), MyJourneysActivity.class);
            intent.putExtra("journey_id", journeyId);
            startActivity(intent);
        } else {

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
