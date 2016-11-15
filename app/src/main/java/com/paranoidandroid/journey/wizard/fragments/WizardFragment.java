package com.paranoidandroid.journey.wizard.fragments;

import android.support.v4.app.Fragment;

import java.util.HashMap;

/**
 * Created by epushkarskaya on 11/14/16.
 */

public abstract class WizardFragment extends Fragment {

    public interface OnItemUpdatedListener {

        public void addData();

    }

    public abstract HashMap<String, Object> getResult();

}
