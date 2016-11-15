package com.paranoidandroid.journey.wizard.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.paranoidandroid.journey.R;
import com.paranoidandroid.journey.wizard.utils.JourneyBuilderUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by epushkarskaya on 11/12/16.
 */

public class NameFragment extends WizardFragment {

    EditText etName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_wizard_name, parent, false);
        etName = (EditText) v.findViewById(R.id.etName);
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
                result.put(JourneyBuilderUtils.NAME_KEY, editable.toString());
                listener.updateJourneyData(result);
            }

        });
        return v;
    }

    @Override
    public boolean readyToPublish() {
        return etName.getText().length() > 0;
    }
}
