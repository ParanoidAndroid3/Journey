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
import com.paranoidandroid.journey.wizard.models.AutoCompleteItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by epushkarskaya on 11/13/16.
 */

public class DestinationAutoCompleteAdapter extends BaseAdapter implements Filterable {

    private static final int MAX_RESULTS = 5;
    private Context mContext;
    private List<AutoCompleteItem> resultList = new ArrayList<>();

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

        AutoCompleteItem item = (AutoCompleteItem) getItem(position);

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
                    List<AutoCompleteItem> destinations = findDestinations(mContext, constraint.toString());

                    // Assign the data to the FilterResults
                    filterResults.values = destinations;
                    filterResults.count = destinations.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    resultList = (List<AutoCompleteItem>) results.values;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }};
        return filter;
    }

    private List<AutoCompleteItem> findDestinations(Context context, String city) {
        // todo: implement background search
        List<AutoCompleteItem> tempResult = new ArrayList<>();
        tempResult.add(new AutoCompleteItem("Paris", "France", 1234));
        tempResult.add(new AutoCompleteItem("Paris", "Kentucky", 5678));
        return tempResult;

    }

}
