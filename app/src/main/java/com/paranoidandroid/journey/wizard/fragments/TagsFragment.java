package com.paranoidandroid.journey.wizard.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.paranoidandroid.journey.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by epushkarskaya on 11/12/16.
 */

public class TagsFragment extends WizardFragment {

    Map<Button, Boolean> sizeButtons;
    Map<Button, Boolean> tagButtons;

    Button btnSolo;
    Button btnCouple;
    Button btnGroup;
    Button btnFamily;

    Button btnCulture;
    Button btnFoodie;
    Button btnAdventure;
    Button btnRelaxation;
    Button btnBudget;
    Button btnLuxury;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sizeButtons = new HashMap<>();
        tagButtons = new HashMap<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_wizard_tags, parent, false);
        setupButtons(v);
        setupButtonGroups(v);
        setupListeners();
        return v;
    }

    @Override
    public HashMap<String, Object> getResult() {
        // todo: pass back result
        return null;
    }

    private void setupButtons(View v) {
        btnSolo = (Button) v.findViewById(R.id.btnSolo);
        btnCouple = (Button) v.findViewById(R.id.btnCouple);
        btnGroup = (Button) v.findViewById(R.id.btnGroup);
        btnFamily = (Button) v.findViewById(R.id.btnFamily);

        btnCulture = (Button) v.findViewById(R.id.btnCulture);
        btnFoodie = (Button) v.findViewById(R.id.btnFoodie);
        btnAdventure = (Button) v.findViewById(R.id.btnAdventure);
        btnRelaxation = (Button) v.findViewById(R.id.btnRelaxation);
        btnBudget = (Button) v.findViewById(R.id.btnBudget);
        btnLuxury = (Button) v.findViewById(R.id.btnLuxury);
    }

    private void setupButtonGroups(View v) {
        sizeButtons.put(btnSolo, false);
        sizeButtons.put(btnCouple, false);
        sizeButtons.put(btnGroup, false);
        sizeButtons.put(btnFamily, false);

        tagButtons.put(btnCulture, false);
        tagButtons.put(btnFoodie, false);
        tagButtons.put(btnAdventure, false);
        tagButtons.put(btnRelaxation, false);
        tagButtons.put(btnBudget, false);
        tagButtons.put(btnLuxury, false);
    }

    private void setupListeners() {
        for (Button button : sizeButtons.keySet()) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    modifySizeButton(view);
                }
            });
        }

        for (Button button : tagButtons.keySet()) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    modifyTagButton(view);
                }
            });
        }
    }

    private void modifySizeButton(View view) {
        Button clickedButton = (Button) view;

        if (sizeButtons.get(clickedButton)) {
            turnOff(sizeButtons, clickedButton);
        } else {
            for (Button button : sizeButtons.keySet()) {
                turnOff(sizeButtons, button);
            }
            turnOn(sizeButtons, clickedButton);
        }
    }

    private void modifyTagButton(View view) {
        Button clickedButton = (Button) view;

        if (tagButtons.get(clickedButton)) {
            turnOff(tagButtons, clickedButton);
        } else {
            turnOn(tagButtons, clickedButton);
        }

    }

    private void turnOff(Map<Button, Boolean> source, Button button) {
       source.put(button, false);
       button.setBackgroundResource(R.color.colorTagNotPressed);
    }

    private void turnOn(Map<Button, Boolean> source, Button button) {
        source.put(button, true);
        button.setBackgroundResource(R.color.colorTagPressed);
    }

}
