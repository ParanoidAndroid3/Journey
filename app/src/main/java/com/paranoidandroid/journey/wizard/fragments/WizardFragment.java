package com.paranoidandroid.journey.wizard.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;

import java.util.Map;

/**
 * Created by epushkarskaya on 11/14/16.
 */

public abstract class WizardFragment extends Fragment {

    protected OnItemUpdatedListener listener;

    /**
     * This method can be called by parent activities
     * to determine if all all required data has been
     * entered.
     */
    public abstract boolean readyToPublish();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnItemUpdatedListener) {
            listener = (OnItemUpdatedListener) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement MyListFragment.OnItemSelectedListener");
        }
    }

    /**
     * This interface allows WizardFragments to
     * communicate with parent activities.
     */
    public interface OnItemUpdatedListener {

        void updateJourneyData(Map<String, Object> data);

        void enableFab(boolean enable);

    }

}
