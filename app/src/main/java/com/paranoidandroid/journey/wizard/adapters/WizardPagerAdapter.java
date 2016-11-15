package com.paranoidandroid.journey.wizard.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.paranoidandroid.journey.wizard.fragments.LegsFragment;
import com.paranoidandroid.journey.wizard.fragments.NameFragment;
import com.paranoidandroid.journey.wizard.fragments.TagsFragment;

/**
 * Created by epushkarskaya on 11/13/16.
 */

public class WizardPagerAdapter extends FragmentPagerAdapter {

    private static final String TAG = "WizardPagerAdapter";

    private int count;

    public WizardPagerAdapter(FragmentManager manager) {
        super(manager);
        this.count = 1;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new NameFragment();
        } else if (position == 1) {
            return new LegsFragment();
        } else if (position == 2) {
            return new TagsFragment();
        } else {
            return null;
        }
    }

    @Override
    public int getCount() {
        return count;
    }

    public void incrementCount() {
        if (count == 3) {
            Log.e(TAG, "Cannot increment view pager count");
        } else {
            count++;
            notifyDataSetChanged();
        }
    }

}