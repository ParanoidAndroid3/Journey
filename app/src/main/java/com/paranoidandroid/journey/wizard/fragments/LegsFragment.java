package com.paranoidandroid.journey.wizard.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.paranoidandroid.journey.R;
import com.paranoidandroid.journey.wizard.adapters.LegItemArrayAdapter;

/**
 * Created by epushkarskaya on 11/12/16.
 */

public class LegsFragment extends WizardFragment {

    ListView lvLegs;
    LegItemArrayAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_wizard_legs, parent, false);
        lvLegs = (ListView) v.findViewById(R.id.lvLegs);
        adapter = new LegItemArrayAdapter(getContext(), listener);
        lvLegs.setAdapter(adapter);

        // We want to start with a single item so that users can input first destination
        adapter.createEmptyRow();

        return v;
    }

    @Override
    public boolean readyToPublish() {
        return adapter.hasValidData();
    }

}
