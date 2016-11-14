package com.paranoidandroid.journey.wizard.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.paranoidandroid.journey.R;

import java.util.HashMap;

/**
 * Created by epushkarskaya on 11/12/16.
 */

public class NameFragment extends WizardFragment {

    EditText etName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_wizard_name, parent, false);
        etName = (EditText) v.findViewById(R.id.etName);
        return v;
    }

    @Override
    public HashMap<String, Object> getResult() {
        // todo: pass back result
        return null;
    }

}
