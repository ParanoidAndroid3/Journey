package com.paranoidandroid.journey.recommendations.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.paranoidandroid.journey.R;
import com.paranoidandroid.journey.models.ui.FoursquareVenue;
import com.paranoidandroid.journey.models.ui.GooglePlace;
import com.paranoidandroid.journey.models.ui.Recommendation;
import com.paranoidandroid.journey.recommendations.interfaces.RecommendationViewHolderClickListener;
import com.paranoidandroid.journey.recommendations.interfaces.RecommendationsListAdapterClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RecommendationsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        RecommendationViewHolderClickListener {

    private RecommendationsListAdapterClickListener listener;
    private final int GOOGLE_PLACE = 0, FOURSQUARE_VENUE = 1;
    private List<Recommendation> items;
    private Context context;

    public void setRecommendationsListAdapterClickListener(RecommendationsListAdapterClickListener listener) {
        this.listener = listener;
    }

    public RecommendationsListAdapter(Context context, List<Recommendation> items) {
        this.items = items;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof GooglePlace) {
            return GOOGLE_PLACE;
        } else if (items.get(position) instanceof FoursquareVenue) {
            return FOURSQUARE_VENUE;
        }
        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case GOOGLE_PLACE:
            case FOURSQUARE_VENUE:
                View v1 = inflater.inflate(R.layout.item_recommendation, parent, false);
                viewHolder = new RecommendationViewHolder(v1, this);
                break;

            default:
                View v = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
                viewHolder = new RecyclerView.ViewHolder(v) { };
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RecommendationViewHolder vh1 = (RecommendationViewHolder) holder;
        switch (holder.getItemViewType()) {
            case GOOGLE_PLACE:
                configureGooglePlaceViewHolder(vh1, position);
                break;
            case FOURSQUARE_VENUE:
                configureFoursquareVenueViewHolder(vh1, position);
                break;
        }
    }

    private void configureGooglePlaceViewHolder(RecommendationViewHolder vh, int position) {
        GooglePlace place = (GooglePlace) items.get(position);
        String imageURL;
        if (place != null) {
            if ((imageURL = place.getImageURL()) != null)
                Glide.with(context)
                    .load(imageURL)
                    .into(vh.photo);
            vh.name.setText(place.getName());
            vh.address.setText(place.getAddress());
            vh.rating.setVisibility(place.getRating() != -1. ? View.VISIBLE : View.GONE);
            vh.rating.setText(String.valueOf(place.getRating()));
        }
    }

    private void configureFoursquareVenueViewHolder(RecommendationViewHolder vh, int position) {
        FoursquareVenue place = (FoursquareVenue) items.get(position);
        String imageURL;
        if (place != null) {
            if ((imageURL = place.getImageURL()) != null)
                Glide.with(context)
                        .load(imageURL)
                        .into(vh.photo);
            vh.name.setText(place.getName());
            vh.address.setText(place.getAddress());
            vh.rating.setVisibility(place.getRating() != -1. ? View.VISIBLE : View.GONE);
            vh.rating.setText(String.valueOf(place.getRating()));
        }
    }

    @Override
    public void onSaveGooglePlaceClicked(int position) {
        // TODO: Implement callbacks
    }

    @Override
    public void onAddGooglePlaceClicked(int position) {
        // TODO: Implement callbacks
    }

    public static class RecommendationViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvName) TextView name;
        @BindView(R.id.ivPhoto) ImageView photo;
        @BindView(R.id.tvAddress) TextView address;
        @BindView(R.id.tvRating) TextView rating;
        @BindView(R.id.ibSave) ImageButton save;
        @BindView(R.id.ibAdd) ImageButton add;
        public RecommendationViewHolderClickListener listener;

        public RecommendationViewHolder(View v, RecommendationViewHolderClickListener listener) {
            super(v);
            ButterKnife.bind(this, v);
            this.listener = listener;
        }

        @OnClick(R.id.ibSave)
        public void onSaveClicked(View v) {
            if (listener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onSaveGooglePlaceClicked(position);
                }
            }
        }

        @OnClick(R.id.ibAdd)
        public void onAddClicked(View v) {
            if (listener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onAddGooglePlaceClicked(position);
                }
            }
        }
    }


}