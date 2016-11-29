package com.paranoidandroid.journey.wizard.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.paranoidandroid.journey.R;
import com.paranoidandroid.journey.models.Destination;
import com.paranoidandroid.journey.models.Journey;
import com.paranoidandroid.journey.models.Leg;
import com.paranoidandroid.journey.network.GooglePlaceSearchClient;
import com.paranoidandroid.journey.wizard.adapters.AutocompleteArrayAdapter;
import com.paranoidandroid.journey.wizard.adapters.LegsArrayAdapter;
import com.paranoidandroid.journey.wizard.models.LegItem;
import com.paranoidandroid.journey.wizard.utils.JourneyBuilder;
import com.paranoidandroid.journey.wizard.utils.PlaceJSONParser;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by epushkarskaya on 11/12/16.
 */

public class LegsFragment extends WizardFragment {

    RecyclerView rvLegs;
    LegsArrayAdapter adapter;
    AutoCompleteTextView atvPlaces;

    public static LegsFragment newInstance(String journeyId) {
        LegsFragment fragment = new LegsFragment();

        Bundle args = new Bundle();
        args.putString("journey_id", journeyId);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_wizard_legs, parent, false);
        rvLegs = (RecyclerView) v.findViewById(R.id.rvLegs);

        if (getArguments() != null) {
            String journeyId = getArguments().getString("journey_id");
            if (journeyId != null) {
                loadJourneyLegs(journeyId);
            } else {
                listener.enableFab(false);
            }
        } else {
            adapter = new LegsArrayAdapter(getContext(), new ArrayList<Leg>(), listener, false);
            listener.enableFab(false);
            rvLegs.setAdapter(adapter);
            rvLegs.setLayoutManager(new LinearLayoutManager(getContext()));
        }

        atvPlaces = (AutoCompleteTextView) v.findViewById(R.id.atvPlaces);
        atvPlaces.setThreshold(1);

        atvPlaces.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
               getPlaces(s.toString(), atvPlaces);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                // nothing
            }
        });

        return v;
    }

    private void getPlaces(String text, final AutoCompleteTextView input){
        GooglePlaceSearchClient.autoComplete(text, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                PlaceJSONParser parser = new PlaceJSONParser();
                List<LegItem> results = parser.parse(response);
                final AutocompleteArrayAdapter acAdapter = new AutocompleteArrayAdapter(getActivity().getBaseContext());
                atvPlaces.setAdapter(acAdapter);
                acAdapter.addAll(results);
                atvPlaces.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        input.setText("");
                        LegItem item = acAdapter.getItem(i);
                        Destination destination = JourneyBuilder.findDestination(item.getPlacesId(), item.getDestination());
                        Leg leg = new Leg();
                        leg.setDestination(destination);
                        adapter.add(leg);
                    }
                });
            }
        });

    }

    private void loadJourneyLegs(String journeyId) {
        ParseQuery<Journey> query = ParseQuery.getQuery(Journey.class);
        query.include("legs");
        query.include("legs.destination");
        query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ONLY);
        query.getInBackground(journeyId, new GetCallback<Journey>() {
            public void done(final Journey journey, ParseException e) {
                if (e == null) {
                    listener.setJourney(journey);
                    adapter = new LegsArrayAdapter(getContext(), journey.getLegs(), listener, true);
                    listener.enableFab(true);
                    rvLegs.setAdapter(adapter);
                    rvLegs.setLayoutManager(new LinearLayoutManager(getContext()));
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

}
