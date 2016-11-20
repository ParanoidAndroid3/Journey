package com.paranoidandroid.journey.wizard.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.paranoidandroid.journey.R;
import com.paranoidandroid.journey.network.GooglePlaceSearchClient;
import com.paranoidandroid.journey.wizard.adapters.AutocompleteArrayAdapter;
import com.paranoidandroid.journey.wizard.adapters.LegItemArrayAdapter;
import com.paranoidandroid.journey.wizard.models.LegItem;
import com.paranoidandroid.journey.wizard.utils.PlaceJSONParser;

import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by epushkarskaya on 11/12/16.
 */

public class LegsFragment extends WizardFragment {

    ListView lvLegs;
    LegItemArrayAdapter adapter;
    AutoCompleteTextView atvPlaces;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_wizard_legs, parent, false);
        lvLegs = (ListView) v.findViewById(R.id.lvLegs);
        adapter = new LegItemArrayAdapter(getContext(), listener);
        lvLegs.setAdapter(adapter);

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
                        adapter.add(acAdapter.getItem(i));
                    }
                });
            }
        });

    }

}
