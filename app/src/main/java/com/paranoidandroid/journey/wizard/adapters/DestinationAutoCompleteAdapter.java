package com.paranoidandroid.journey.wizard.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.paranoidandroid.journey.R;
import com.paranoidandroid.journey.wizard.models.DestinationItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by epushkarskaya on 11/13/16.
 */

public class DestinationAutoCompleteAdapter extends BaseAdapter implements Filterable {

    private static final int MAX_RESULTS = 5;
    private Context mContext;
    private List<DestinationItem> resultList = new ArrayList<>();

    public DestinationAutoCompleteAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public Object getItem(int position) {
        return resultList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_destination_ac_results, parent, false);
        }

        DestinationItem item = (DestinationItem) getItem(position);

        ((TextView) convertView.findViewById(R.id.tvCity)).setText(item.getCity());
        ((TextView) convertView.findViewById(R.id.tvCountry)).setText(item.getCountry());

        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    List<DestinationItem> destinations = findDestinations(mContext, constraint.toString());

                    // Assign the data to the FilterResults
                    filterResults.values = destinations;
                    filterResults.count = destinations.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    resultList = (List<DestinationItem>) results.values;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }};
        return filter;
    }

    private List<DestinationItem> findDestinations(Context context, String city) {

        // todo: implement background search

        List<DestinationItem> tempResult = new ArrayList<>();
        tempResult.add(new DestinationItem("Paris", "France", 1, 2));
        tempResult.add(new DestinationItem("Paris", "Kentucky", 5, 6));
        return tempResult;

        //https://maps.googleapis.com/maps/api/place/autocomplete/json?input=Paris&types=geocode&key=YOUR_API_KEY


    }

}
