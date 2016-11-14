package com.paranoidandroid.journey.wizard.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;

import com.paranoidandroid.journey.R;
import com.paranoidandroid.journey.wizard.models.LegItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

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

        }

        holder = (ViewHolder) convertView.getTag();

        holder.tvDestination.setText(leg.getDestination());
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
            button.setBackgroundResource(R.drawable.ic_calendar);
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

    private class ViewHolder {
        ImageButton btnDeleteLeg;
        AutoCompleteTextView tvDestination;
        Button btnStartDate;
        Button btnEndDate;

        public ViewHolder(View view) {
            this.btnDeleteLeg = (ImageButton) view.findViewById(R.id.btnDeleteLeg);
            this.tvDestination = (AutoCompleteTextView) view.findViewById(R.id.tvDestination);
            this.btnStartDate = (Button) view.findViewById(R.id.btnStartDate);
            this.btnEndDate = (Button) view.findViewById(R.id.btnEndDate);
        }
    }

}
