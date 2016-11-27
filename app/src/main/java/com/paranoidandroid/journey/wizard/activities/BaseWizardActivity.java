package com.paranoidandroid.journey.wizard.activities;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.paranoidandroid.journey.R;
import com.paranoidandroid.journey.models.Leg;
import com.paranoidandroid.journey.wizard.fragments.WizardFragment;
import com.paranoidandroid.journey.wizard.utils.JourneyBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by epushkarskaya on 11/13/16.
 */

public abstract class BaseWizardActivity extends AppCompatActivity implements WizardFragment.OnItemUpdatedListener {

    protected Map<String, Object> journeyData;
    protected FloatingActionButton fab;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        journeyData = new HashMap<>();
    }

    public void onExit(View view) {
        finish();
    }

    protected boolean nameComplete() {
        if (!journeyData.containsKey(JourneyBuilder.NAME_KEY)){
            return false;
        }
        return !((String) journeyData.get(JourneyBuilder.NAME_KEY)).isEmpty();
    }

    protected boolean legsComplete() {
        if (!journeyData.containsKey(JourneyBuilder.LEGS_KEY)){
            return false;
        }
        List<Leg> legs = (List<Leg>) journeyData.get(JourneyBuilder.LEGS_KEY);
        for (Leg leg : legs) {
            if (leg.getDestination() == null) {
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

    protected boolean tagsComplete() {
        if (!journeyData.containsKey(JourneyBuilder.SIZE_KEY)){
            return false;
        }
        return !((String) journeyData.get(JourneyBuilder.SIZE_KEY)).isEmpty();
    }


    // ------ WizardFragment.OnItemUpdatedListener implementation ------- //

    @Override
    public void updateJourneyData(Map<String, Object> data) {
        journeyData.putAll(data);
    }

    @Override
    public void enableFab(boolean enable) {
        fab.setClickable(enable);
        int resource = enable ? R.color.colorFabEnabled : R.color.colorFabDisabled;
        int color = getResources().getColor(resource);
        fab.setBackgroundTintList(ColorStateList.valueOf(color));
    }

}
