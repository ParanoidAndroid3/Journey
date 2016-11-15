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

import com.paranoidandroid.journey.MyJourneysActivity;
import com.paranoidandroid.journey.R;
import com.paranoidandroid.journey.wizard.adapters.WizardPagerAdapter;
import com.paranoidandroid.journey.wizard.utils.JourneyBuilderUtils;

import java.util.HashMap;

public class WizardActivity extends AppCompatActivity {

    private static final String TAG = "WizardActivity";

    private WizardPagerAdapter pagerAdapter;
    private ViewPager viewpager;
    private HashMap<String, Object> journeyData;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wizard);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // todo: accumulate data from fragment and add to journey parts

                int currentFragment = viewpager.getCurrentItem();
                if (currentFragment < 2) {
                    goToNextFragment(currentFragment);
                } else {
                    Toast.makeText(getApplicationContext(), "Complete!", Toast.LENGTH_LONG).show(); // todo: remove

                    int journeyId = JourneyBuilderUtils.buildJourney(journeyData);

                    Intent intent = new Intent(getApplicationContext(), MyJourneysActivity.class); // todo: change this to map view activity
                    intent.putExtra("journey_id", journeyId);
                    startActivity(intent);
                }
            }
        });

        viewpager = (ViewPager) findViewById(R.id.viewpager);
        pagerAdapter = new WizardPagerAdapter(getSupportFragmentManager());
        viewpager.setAdapter(pagerAdapter);

        journeyData = new HashMap<>();

    }

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

}
