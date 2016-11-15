package com.paranoidandroid.journey.wizard.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.paranoidandroid.journey.R;
import com.paranoidandroid.journey.network.GooglePlaceSearchClient;
import com.paranoidandroid.journey.wizard.models.AutoCompleteItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by epushkarskaya on 11/13/16.
 */

public class DestinationAutoCompleteAdapter extends BaseAdapter implements Filterable {

    private static final String TAG = "DestinationACAdapter";

    private Context mContext;

    private List<AutoCompleteItem> resultList = new ArrayList<>();
    private List<AutoCompleteItem> rawResults = new ArrayList<>();

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

        ((TextView) convertView.findViewById(R.id.tvDescription)).setText(item.getDescription());

        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    findDestinations(constraint.toString());

                    // Assign the data to the FilterResults
                    filterResults.values = rawResults;
                    filterResults.count = rawResults.size();
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

    /**
     * This method is responsible for making an async network call to GooglePlaces api.
     */
    private void findDestinations(String input) {

        GooglePlaceSearchClient.autoComplete(input, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                try {
                    JSONArray predictions = json.getJSONArray("predictions");
                    for (int i = 0; i < predictions.length(); i++) {
                        AutoCompleteItem item = new AutoCompleteItem(predictions.getJSONObject(i));
                        rawResults.add(item);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TAG, errorResponse.toString());
            }
        });

    }

}
