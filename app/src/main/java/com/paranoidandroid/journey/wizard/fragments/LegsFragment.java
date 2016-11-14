package com.paranoidandroid.journey.wizard.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.paranoidandroid.journey.R;
import com.paranoidandroid.journey.wizard.adapters.LegItemArrayAdapter;

import java.util.HashMap;

/**
 * Created by epushkarskaya on 11/12/16.
 */

public class LegsFragment extends WizardFragment {

    ListView lvLegs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_wizard_legs, parent, false);
        lvLegs = (ListView) v.findViewById(R.id.lvLegs);
        lvLegs.setAdapter(new LegItemArrayAdapter(getActivity()));
        return v;
    }

    @Override
    public HashMap<String, Object> getResult() {
        // todo: pass back result
        return null;
    }
}
