package com.paranoidandroid.journey.wizard.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.paranoidandroid.journey.R;
import com.paranoidandroid.journey.wizard.utils.JourneyBuilderUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by epushkarskaya on 11/12/16.
 */

public class TagsFragment extends WizardFragment {

    /**
     * Buttons that describe the size of the travel group
     **/
    Map<Button, Boolean> sizeButtons;

    /**
     * Buttons that describe the interests of the traveler(s)
     **/
    Map<Button, Boolean> tagButtons;

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
        setupListeners();
        return v;
    }

    /**
     * This method finds all button views and
     * adds them to the appropriate map
     */
    private void setupButtons(View v) {
        // find first set of button views
        Button btnSolo = (Button) v.findViewById(R.id.btnSolo);
        Button btnCouple = (Button) v.findViewById(R.id.btnCouple);
        Button btnGroup = (Button) v.findViewById(R.id.btnGroup);
        Button btnFamily = (Button) v.findViewById(R.id.btnFamily);

        sizeButtons.put(btnSolo, false);
        sizeButtons.put(btnCouple, false);
        sizeButtons.put(btnGroup, false);
        sizeButtons.put(btnFamily, false);

        Button btnCulture = (Button) v.findViewById(R.id.btnCulture);
        Button btnFoodie = (Button) v.findViewById(R.id.btnFoodie);
        Button btnAdventure = (Button) v.findViewById(R.id.btnAdventure);
        Button btnRelaxation = (Button) v.findViewById(R.id.btnRelaxation);
        Button btnBudget = (Button) v.findViewById(R.id.btnBudget);
        Button btnLuxury = (Button) v.findViewById(R.id.btnLuxury);

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
                    updateParent();
                }
            });
        }

        for (Button button : tagButtons.keySet()) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    modifyTagButton(view);
                    updateParent();
                }
            });
        }
    }

    /**
     * Only one size button can be selected at a time.
     */
    private void modifySizeButton(View view) {
        Button clickedButton = (Button) view;

        if (sizeButtons.get(clickedButton)) {
            turnOff(sizeButtons, clickedButton);
            listener.enableFab(false);
        } else {
            for (Button button : sizeButtons.keySet()) {
                turnOff(sizeButtons, button);
            }
            turnOn(sizeButtons, clickedButton);
            listener.enableFab(true);
        }
    }

    /**
     * Any number [0, n] of tags can be selected at a time.
     */
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

    /**
     * Communicates the size selection and a list of tags to the parent activity
     */
    private void updateParent() {
        Map<String, Object> result = new HashMap<>();
        for (Button button : sizeButtons.keySet()) {
            if (sizeButtons.get(button)) {
                result.put(JourneyBuilderUtils.SIZE_KEY, button.getText().toString());
            }
        }

        List<String> tags = new ArrayList<>();
        for (Button button :tagButtons.keySet()) {
            if (tagButtons.get(button)) {
                tags.add(button.getText().toString());
            }
        }
        result.put(JourneyBuilderUtils.TAGS_KEY, tags);
        listener.updateJourneyData(result);
    }

    /**
     * The Journey cannot be published if the group size has not been specified.
     */
    @Override
    public boolean readyToPublish() {
        return sizeButtons.values().contains(Boolean.TRUE);
    }

}
