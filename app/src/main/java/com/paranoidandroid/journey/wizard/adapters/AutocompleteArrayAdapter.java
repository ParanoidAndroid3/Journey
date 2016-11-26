package com.paranoidandroid.journey.wizard.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.paranoidandroid.journey.R;
import com.paranoidandroid.journey.wizard.models.LegItem;

import java.util.ArrayList;

/**
 * Created by epushkarskaya on 11/19/16.
 */

public class AutocompleteArrayAdapter extends ArrayAdapter<LegItem> {

    public AutocompleteArrayAdapter(Context context) {
        super(context, 0, new ArrayList<LegItem>());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LegItem item = getItem(position);
        AutocompleteArrayAdapter.ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_auto_complete_result, parent, false);
            holder = new AutocompleteArrayAdapter.ViewHolder(convertView);
            convertView.setTag(holder);
        }

        holder = (AutocompleteArrayAdapter.ViewHolder) convertView.getTag();
        holder.tvPlace.setText(item.getDestination());
        return convertView;
    }

    private class ViewHolder {
        TextView tvPlace;

        public ViewHolder(View view) {
            this.tvPlace = (TextView) view.findViewById(R.id.tvPlace);
        }
    }
}
