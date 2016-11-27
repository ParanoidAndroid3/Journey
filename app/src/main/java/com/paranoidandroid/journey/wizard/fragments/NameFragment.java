package com.paranoidandroid.journey.wizard.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.paranoidandroid.journey.R;
import com.paranoidandroid.journey.models.Journey;
import com.paranoidandroid.journey.wizard.utils.JourneyBuilder;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by epushkarskaya on 11/12/16.
 */

public class NameFragment extends WizardFragment {

    EditText etName;

    public static NameFragment newInstance(String journeyId) {
        NameFragment fragment = new NameFragment();

        Bundle args = new Bundle();
        args.putString("journey_id", journeyId);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_wizard_name, parent, false);
        etName = (EditText) v.findViewById(R.id.etName);

        if (getArguments() != null) {
            String journeyId = getArguments().getString("journey_id");
            if (journeyId != null) {
                loadJourneyName(journeyId);
            } else {
                listener.enableFab(false);
            }
        } else {
           listener.enableFab(false);
        }

        etName.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // nothing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                listener.enableFab(charSequence.length() > 0);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Map<String, Object> result = new HashMap<>();
                result.put(JourneyBuilder.NAME_KEY, editable.toString());
                listener.updateJourneyData(result);
            }

        });
        return v;
    }

    private void loadJourneyName(String journeyId) {
        ParseQuery<Journey> query = ParseQuery.getQuery(Journey.class);
        query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ONLY);
        query.getInBackground(journeyId, new GetCallback<Journey>() {
            public void done(final Journey journey, ParseException e) {
                if (e == null) {
                    listener.setJourney(journey);
                    etName.setText(journey.getName());
                    listener.enableFab(true);
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

}
