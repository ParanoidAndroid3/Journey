package com.paranoidandroid.journey.wizard.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.paranoidandroid.journey.models.Journey;

import java.util.Map;

/**
 * Created by epushkarskaya on 11/14/16.
 */

public abstract class WizardFragment extends Fragment {

    protected OnItemUpdatedListener updateListener;
    protected LoadingListener loadingListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof  LoadingListener && context instanceof OnItemUpdatedListener) {
            updateListener = (OnItemUpdatedListener) context;
            loadingListener = (LoadingListener) context;
        }
        else if (context instanceof OnItemUpdatedListener) {
            updateListener = (OnItemUpdatedListener) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement MyListFragment.OnItemSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        updateListener = null;
    }

    void animatePrompt(View view) {
        YoYo.with(Techniques.BounceInDown)
                .duration(700)
                .playOn(view);
    }

    /**
     * This interface allows WizardFragments to
     * communicate with parent activities.
     */
    public interface OnItemUpdatedListener {

        void updateJourneyData(Map<String, Object> data);

        void enableFab(boolean enable);

        void setJourney(Journey journey);

        Journey getJourney();

    }

    /**
     * This interface allows WizardFragments to
     * turn on/off loading bars
     */
    public interface LoadingListener {

        void hideLoading();

        void showLoading();

    }

}
