package com.paranoidandroid.journey.wizard.adapters;

import android.content.Context;
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
import com.paranoidandroid.journey.wizard.models.AutoCompleteItem;
import com.paranoidandroid.journey.wizard.models.LegItem;
import com.paranoidandroid.journey.wizard.utils.DelayAutoCompleteTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.paranoidandroid.journey.R.id.etDestination;

/**
 * Created by epushkarskaya on 11/13/16.
 */

public class LegItemArrayAdapter extends ArrayAdapter<LegItem> {

    public LegItemArrayAdapter(Context context) {
        super(context, 0, new ArrayList<LegItem>());
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

        if (leg.getCity() != null) {
            holder.etDestination.setText(leg.getCity() + ", " + leg.getCountry());
            setVisibility(holder, View.VISIBLE);
        } else {
            setVisibility(holder, View.GONE);
        }

        assignDate(holder.btnStartDate, leg.getStartDate());
        assignDate(holder.btnEndDate, leg.getEndDate());

        return convertView;
    }

    /**
     * This method either shows the selected date on the button or
     * sets the background resource to the calendar icon
     */
    private void assignDate(Button button, Date date) {
        if (date == null) {
            button.setText("");
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
            button.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_calendar));
        } else {
            String str = new SimpleDateFormat("MM/dd", Locale.US).format(date);
            button.setText(str);
            button.setBackgroundResource(0);
        }
    }

    private void onDeleteClick(int position) {
        remove(getItem(position));
        notifyDataSetChanged();
    }

    private void onCalendarClick(View view) {
        // todo: handle calendar click
    }

    private void setVisibility(ViewHolder holder, int visibility) {
        holder.btnDeleteLeg.setVisibility(visibility);
        holder.btnStartDate.setVisibility(visibility);
        holder.btnEndDate.setVisibility(visibility);
        if (visibility == View.VISIBLE) {
            holder.etDestination.setBackgroundResource(0);
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
                destination.setText(autoCompleteItem.getCity() + ", " + autoCompleteItem.getCountry());
                legItem.setCity(autoCompleteItem.getCity());
                legItem.setCountry(autoCompleteItem.getCountry());
                legItem.setId(autoCompleteItem.getId());
                legItem.setVisible(true);
                setVisibility(holder, View.VISIBLE);
                createEmptyRow();
            }
        });

    }

    private void createEmptyRow() {
        add(new LegItem());
        notifyDataSetChanged();
    }

    private class ViewHolder {
        ImageButton btnDeleteLeg;
        DelayAutoCompleteTextView etDestination;
        Button btnStartDate;
        Button btnEndDate;

        public ViewHolder(View view) {
            this.btnDeleteLeg = (ImageButton) view.findViewById(R.id.btnDeleteLeg);
            this.etDestination = (DelayAutoCompleteTextView) view.findViewById(R.id.etDestination);
            this.btnStartDate = (Button) view.findViewById(R.id.btnStartDate);
            this.btnEndDate = (Button) view.findViewById(R.id.btnEndDate);
        }
    }

}
