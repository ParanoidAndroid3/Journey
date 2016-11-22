package com.paranoidandroid.journey.wizard.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.paranoidandroid.journey.R;
import com.paranoidandroid.journey.wizard.utils.JourneyBuilder;

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
    Map<String, Boolean> sizeStates;
    List<Button> sizeButtons;


    /**
     * Buttons that describe the interests of the traveler(s)
     **/
    Map<String, Boolean> tagStates;
    List<Button> tagButtons;


    public static TagsFragment newInstance(String journeyId) {
        TagsFragment fragment = new TagsFragment();

        Bundle args = new Bundle();
        args.putString("journeyId", journeyId);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sizeStates = new HashMap<>();
        tagStates = new HashMap<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_wizard_tags, parent, false);
        sizeButtons = new ArrayList<>();
        tagButtons = new ArrayList<>();
        setupButtons(v);
        setupStates();
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

        sizeButtons.add(btnSolo);
        sizeButtons.add(btnCouple);
        sizeButtons.add(btnGroup);
        sizeButtons.add(btnFamily);

        Button btnCulture = (Button) v.findViewById(R.id.btnCulture);
        Button btnFoodie = (Button) v.findViewById(R.id.btnFoodie);
        Button btnAdventure = (Button) v.findViewById(R.id.btnAdventure);
        Button btnRelaxation = (Button) v.findViewById(R.id.btnRelaxation);
        Button btnBudget = (Button) v.findViewById(R.id.btnBudget);
        Button btnLuxury = (Button) v.findViewById(R.id.btnLuxury);

        tagButtons.add(btnCulture);
        tagButtons.add(btnFoodie);
        tagButtons.add(btnAdventure);
        tagButtons.add(btnRelaxation);
        tagButtons.add(btnBudget);
        tagButtons.add(btnLuxury);
    }

    private void setupStates() {
        if (tagStates.size() == 0) {
            for (Button button : tagButtons) {
                tagStates.put(button.getText().toString(), false);
            }
        } else {
            for (Button button : tagButtons) {
                if (tagStates.get(button.getText().toString())) {
                    turnOn(tagStates, button);
                }
            }
        }

        if (sizeStates.size() == 0) {
            for (Button button : sizeButtons) {
                sizeStates.put(button.getText().toString(), false);
            }
        } else {
            for (Button button : sizeButtons) {
                if (sizeStates.get(button.getText().toString())) {
                    turnOn(sizeStates, button);
                }
            }
        }
    }

    private void setupListeners() {
        for (Button button : sizeButtons) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    modifySizeButton(view);
                    updateParent();
                }
            });
        }

        for (Button button : tagButtons) {
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

        if (sizeStates.get(clickedButton.getText().toString())) {
            turnOff(sizeStates, clickedButton);
            listener.enableFab(false);
        } else {
            for (Button button : sizeButtons) {
                turnOff(sizeStates, button);
            }
            turnOn(sizeStates, clickedButton);
            listener.enableFab(true);
        }
    }

    /**
     * Any number [0, n] of tags can be selected at a time.
     */
    private void modifyTagButton(View view) {
        Button clickedButton = (Button) view;

        if (tagStates.get(clickedButton.getText().toString())) {
            turnOff(tagStates, clickedButton);
        } else {
            turnOn(tagStates, clickedButton);
        }
    }

    private void turnOff(Map<String, Boolean> source, Button button) {
       source.put(button.getText().toString(), false);
       button.setBackgroundResource(R.color.colorTagNotPressed);
    }

    private void turnOn(Map<String, Boolean> source, Button button) {
        source.put(button.getText().toString(), true);
        button.setBackgroundResource(R.color.colorTagPressed);
    }

    /**
     * Communicates the size selection and a list of tags to the parent activity
     */
    private void updateParent() {
        Map<String, Object> result = new HashMap<>();
        for (Button button : sizeButtons) {
            if (sizeStates.get(button.getText().toString())) {
                result.put(JourneyBuilder.SIZE_KEY, button.getText().toString());
            }
        }

        List<String> tags = new ArrayList<>();
        for (Button button : tagButtons) {
            if (tagStates.get(button.getText().toString())) {
                tags.add(button.getText().toString());
            }
        }
        result.put(JourneyBuilder.TAGS_KEY, tags);
        listener.updateJourneyData(result);
    }

}
