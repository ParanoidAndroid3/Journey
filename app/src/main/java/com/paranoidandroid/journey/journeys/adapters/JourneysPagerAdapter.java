package com.paranoidandroid.journey.journeys.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.astuetz.PagerSlidingTabStrip;
import com.paranoidandroid.journey.R;
import com.paranoidandroid.journey.journeys.fragments.AllJourneysListFragment;
import com.paranoidandroid.journey.journeys.fragments.JourneysListFragment;
import com.paranoidandroid.journey.journeys.fragments.MyJourneysListFragment;

/**
 * Created by epushkarskaya on 12/6/16.
 */

public class JourneysPagerAdapter extends FragmentPagerAdapter implements PagerSlidingTabStrip.IconTabProvider {

    private JourneysListFragment currentFragment = null;

    private int[] tabIcons = {R.drawable.ic_my_journeys, R.drawable.ic_all_journeys};

    public JourneysPagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            currentFragment = MyJourneysListFragment.newInstance();
        } else if (position == 1) {
            currentFragment = AllJourneysListFragment.newInstance();
        } else {
            currentFragment = null;
        }
        return currentFragment;
    }

    @Override
    public int getCount() {
        return tabIcons.length;
    }

    @Override
    public int getPageIconResId(int position) {
        return tabIcons[position];
    }

    public JourneysListFragment getCurrentFragment() {
        return currentFragment;
    }

}
