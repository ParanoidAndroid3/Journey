package com.paranoidandroid.journey.wizard.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDelegate;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;

import com.paranoidandroid.journey.R;
import com.paranoidandroid.journey.wizard.fragments.WizardFragment;
import com.paranoidandroid.journey.wizard.models.AutoCompleteItem;
import com.paranoidandroid.journey.wizard.models.LegItem;
import com.paranoidandroid.journey.wizard.utils.DelayAutoCompleteTextView;
import com.paranoidandroid.journey.wizard.utils.JourneyBuilderUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.paranoidandroid.journey.R.id.etDestination;

/**
 * Created by epushkarskaya on 11/13/16.
 */

public class LegItemArrayAdapter extends ArrayAdapter<LegItem> {

    private static Drawable originalEtDrawable = null;

    private WizardFragment.OnItemUpdatedListener listener;

    public LegItemArrayAdapter(Context context, WizardFragment.OnItemUpdatedListener listener) {
        super(context, 0, new ArrayList<LegItem>());
        this.listener = listener;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LegItem leg = getItem(position);
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_leg_definition, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);

            holder.btnDeleteLeg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onDeleteClick(position);
                }
            });

            holder.btnStartDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onCalendarClick(view);
                }
            });

            holder.btnEndDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onCalendarClick(view);
                }
            });

            setupAutocomplete(convertView, holder, leg);

        }

        holder = (ViewHolder) convertView.getTag();

        // If no city has been selected yet, we do not want show calendars.
        if (leg.getDestination() != null) {
            holder.etDestination.setText(leg.getDestination());
            setVisibility(holder, View.VISIBLE);
        } else {
            holder.etDestination.setText("");
            setVisibility(holder, View.INVISIBLE);
        }

        assignDate(holder.btnStartDate, leg.getStartDate());
        assignDate(holder.btnEndDate, leg.getEndDate());

        return convertView;
    }

    /**
     * Override method to include an update to parent activity
     */
    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();

        List<LegItem> legs = new ArrayList<>();
        for (int i = 0; i < getCount() - 1; i++){
            legs.add(getItem(i));
        }

        if (getCount() > 1) {
            Map<String, Object> result = new HashMap<>();
            result.put(JourneyBuilderUtils.LEGS_KEY, legs);
            listener.updateJourneyData(result);
        }
    }

    /**
     * This method either shows the selected date on the button or sets the background
     * resource to the calendar icon.
     */
    private void assignDate(Button button, Date date) {
        if (date == null && !button.getText().toString().equals("")) {
            button.setText("");
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
            button.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_calendar));
        } else if (date != null && button.getText().toString().equals("")){
            String str = new SimpleDateFormat("MM/dd", Locale.US).format(date);
            button.setText(str);
            button.setBackgroundResource(0);
        }
    }

    private void onDeleteClick(int position) {
        remove(getItem(position));
    }

    /**
     * Presents a large calendar view for user to select travel dates.
     */
    private void onCalendarClick(View view) {
        // todo: handle calendar click
    }

    /**
     * Used to show or hide calendar views and delete button.
     */
    private void setVisibility(ViewHolder holder, int visibility) {
        holder.btnDeleteLeg.setVisibility(visibility);
        holder.btnStartDate.setVisibility(visibility);
        holder.btnEndDate.setVisibility(visibility);

        if (visibility == View.VISIBLE) {
            holder.etDestination.setBackgroundResource(0);
            holder.btnDeleteLeg.setClickable(true);
            holder.btnStartDate.setClickable(true);
            holder.btnEndDate.setClickable(true);
        } else {
            holder.etDestination.setBackground(originalEtDrawable);
            holder.btnDeleteLeg.setClickable(false);
            holder.btnStartDate.setClickable(false);
            holder.btnEndDate.setClickable(false);
        }
    }

    private void setupAutocomplete(View view, final ViewHolder holder, final LegItem legItem) {
        final DelayAutoCompleteTextView destination = (DelayAutoCompleteTextView) view.findViewById(etDestination);
        destination.setThreshold(3);
        destination.setAdapter(new DestinationAutoCompleteAdapter(getContext()));
        destination.setLoadingIndicator(
                (android.widget.ProgressBar) view.findViewById(R.id.pb_loading_indicator));
        destination.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                AutoCompleteItem autoCompleteItem = (AutoCompleteItem) adapterView.getItemAtPosition(position);
                destination.setText(autoCompleteItem.getDescription());
                legItem.setDestination(autoCompleteItem.getDescription());
                legItem.setPlacesId(autoCompleteItem.getPlaceId());
                legItem.setVisible(true);
                setVisibility(holder, View.VISIBLE);
                createEmptyRow();
            }
        });

    }

    /**
     * This method is used to create and empty row to the list view, if one does not already exist.
     * Users will use this row to add another destination to their route
     */
    public void createEmptyRow() {
        int index = getCount() - 1;
        if (index < 0 || getItem(index).isVisible()) {
            add(new LegItem());
        }
    }

    /**
     * This method returns whether or not all of the data in the list is valid.
     */
    public boolean hasValidData() {
        int numLegs = getCount() -1;
        if (numLegs == 0) {
            return false;
        }
        for (int i = 0; i < numLegs; i++) {
            if (!isLegValid(getItem(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * A leg item is valid if a destination id, start date and end date are all defined.
     * If item is not visible then there is an issue and we will not count list as valid.
     */
    private boolean isLegValid(LegItem item) {
       return item.getPlacesId() != null && item.getStartDate() != null && item.getEndDate() != null && item.isVisible();
    }

    private class ViewHolder {

        ImageButton btnDeleteLeg;
        DelayAutoCompleteTextView etDestination;
        Button btnStartDate;
        Button btnEndDate;

        public ViewHolder(View view) {
            this.btnDeleteLeg = (ImageButton) view.findViewById(R.id.btnDeleteLeg);
            this.etDestination = (DelayAutoCompleteTextView) view.findViewById(R.id.etDestination);
            if (originalEtDrawable == null) {
                originalEtDrawable = etDestination.getBackground();
            }
            this.btnStartDate = (Button) view.findViewById(R.id.btnStartDate);
            this.btnEndDate = (Button) view.findViewById(R.id.btnEndDate);
        }
    }

}
