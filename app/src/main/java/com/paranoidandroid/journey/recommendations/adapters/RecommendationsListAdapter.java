package com.paranoidandroid.journey.recommendations.adapters;

import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.maps.model.LatLng;
import com.paranoidandroid.journey.R;
import com.paranoidandroid.journey.models.ui.FoursquareVenue;
import com.paranoidandroid.journey.models.ui.GooglePlace;
import com.paranoidandroid.journey.models.ui.Recommendation;
import com.paranoidandroid.journey.recommendations.interfaces.RecommendationViewHolderClickListener;
import com.paranoidandroid.journey.recommendations.interfaces.RecommendationsListAdapterClickListener;
import com.paranoidandroid.journey.recommendations.viewholders.ViewHolders;
import com.paranoidandroid.journey.support.MapUtils;
import com.paranoidandroid.journey.support.ui.DynamicHeightImageView;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.paranoidandroid.journey.recommendations.viewholders.ViewHolders.RecommendationViewHolder;

public class RecommendationsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        RecommendationViewHolderClickListener {

    private RecommendationsListAdapterClickListener listener;
    private final int GOOGLE_PLACE = 0, FOURSQUARE_VENUE = 1;
    private List<Recommendation> items;
    private Context context;
    private LatLng headCoordinates;
    private static DecimalFormat distFormat = new DecimalFormat("#.#");

    public void setRecommendationsListAdapterClickListener(RecommendationsListAdapterClickListener listener) {
        this.listener = listener;
    }

    public RecommendationsListAdapter(Context context, List<Recommendation> items, LatLng headCoordinates) {
        this.items = items;
        this.context = context;
        this.headCoordinates = headCoordinates;
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
        if (place != null) {
            vh.photo.setHeightRatio(1);
            Glide.with(context)
                    .load(place.getImageUrl())
                    .placeholder(ContextCompat.getDrawable(context, R.drawable.ic_placeholder))
                    .error(ContextCompat.getDrawable(context, R.drawable.ic_placeholder))
                    .into(vh.photo);
            vh.name.setText(place.getName());
            vh.distance.setText(distFormat.format(
                    MapUtils.haversine(place.getLatitude(), place.getLongitude(), this.headCoordinates.latitude, this.headCoordinates.longitude)
                    ) + "km");
            vh.save.setImageResource(place.isBookmarked() ? R.drawable.ic_bookmark_activity_selected : R.drawable.ic_bookmark_activity_normal);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ViewCompat.setTransitionName(vh.name, "T"+place.getId());
                ViewCompat.setTransitionName(vh.photo, "P"+place.getId());
            }
        }
    }

    private void configureFoursquareVenueViewHolder(RecommendationViewHolder vh, int position) {
        FoursquareVenue place = (FoursquareVenue) items.get(position);
        if (place != null) {
            vh.photo.setHeightRatio(1);
            Glide.with(context)
                    .load(place.getImageUrl())
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .placeholder(ContextCompat.getDrawable(context, R.drawable.ic_placeholder))
                    .error(ContextCompat.getDrawable(context, R.drawable.ic_placeholder))
                    .into(vh.photo);
            vh.name.setText(place.getName());
            vh.distance.setText(distFormat.format(
                    MapUtils.haversine(place.getLatitude(), place.getLongitude(), this.headCoordinates.latitude, this.headCoordinates.longitude)
            ) + "km");
            vh.save.setImageResource(place.isBookmarked() ? R.drawable.ic_bookmark_activity_selected : R.drawable.ic_bookmark_activity_normal);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ViewCompat.setTransitionName(vh.name, "T"+place.getId());
                ViewCompat.setTransitionName(vh.photo, "P"+place.getId());
            }
        }
    }

    @Override
    public void onAddBookmarkClickedAt(int position) {
        if (this.listener != null) {
            if (items.get(position).isBookmarked()) {
                this.listener.onRemoveBookmarkClicked(items.get(position), position);
            } else {
                this.listener.onAddBookmarkClicked(items.get(position), position);
            }
        }
    }
}