package com.paranoidandroid.journey.myjourneys.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.paranoidandroid.journey.R;
import com.paranoidandroid.journey.databinding.ItemJourneyBinding;
import com.paranoidandroid.journey.models.Destination;
import com.paranoidandroid.journey.models.Journey;
import com.paranoidandroid.journey.models.Leg;
import com.paranoidandroid.journey.models.ui.GooglePlace;
import com.paranoidandroid.journey.support.DateFormattingUtils;

import java.util.Collection;
import java.util.List;

public class JourneyAdapter extends RecyclerView.Adapter<JourneyAdapter.ViewHolder> {
    private static final String TAG = "JourneyAdapter";
    private static final int DEFAULT_SCREEN_WIDTH = 1080;

    public interface OnItemSelectedListener {
        void onItemSelected(Journey journey);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ItemJourneyBinding binding;

        ViewHolder(ItemJourneyBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                selectItemAtPosition(position);
            }
        }
    }

    private List<Journey> items;
    private OnItemSelectedListener listener;
    private int screenWidth;

    public JourneyAdapter(List<Journey> items) {
        this.items = items;
        this.screenWidth = DEFAULT_SCREEN_WIDTH;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemJourneyBinding binding = ItemJourneyBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Journey journey = items.get(position);
        holder.binding.setJourney(journey);

        Context context = holder.itemView.getContext();
        CharSequence dateRange = DateFormattingUtils.formatDateRange(
                context, journey.getStartDate(), journey.getEndDate());

        holder.binding.tvDuration.setText(dateRange);

        // TODO(emmanuel): Move this code to a custom view so we can cache inflated layouts.
        holder.binding.llLegs.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(holder.itemView.getContext());
        for (Leg leg : journey.getLegs()) {
            View view = inflater.inflate(R.layout.item_journey_leg, holder.binding.llLegs, false);
            TextView tvDestination = (TextView) view.findViewById(R.id.tvDestination);
            TextView tvDuration = (TextView) view.findViewById(R.id.tvDuration);

            tvDestination.setText(leg.getDestination().getCityName());

            CharSequence duration = DateFormattingUtils.formatDurationInDays(
                    leg.getStartDate(), leg.getEndDate());
            tvDuration.setText(duration);

            holder.binding.llLegs.addView(view);
        }

        holder.binding.ivBackdrop.setImageResource(R.drawable.image_placeholder);
        loadBackdropImage(holder, journey);
    }

    private void loadBackdropImage(final ViewHolder holder, Journey journey) {
        Context context = holder.itemView.getContext();

        if (journey.getLegs().size() == 0) {
            Log.e(TAG, "Can't load backdrop: no legs on Journey.");
            return;
        }

        final Destination destination = journey.getLegs().get(0).getDestination();
        String imageReference = destination.getCachedImageReference();

        if (imageReference == null) {
            String placeId = destination.getGooglePlaceId();
            GooglePlace.getPlaceImageAsync(placeId, new GooglePlace.OnImageReferenceReadyHandler() {
                @Override
                public void onImageReferenceReady(String imageReference) {
                    destination.setCachedImageReference(imageReference);
                    destination.saveEventually();
                    notifyItemChanged(holder.getAdapterPosition());
                }

                @Override
                public void onFailure(Throwable throwable) {
                    Log.e(TAG, "Unable to get backdrop image", throwable);
                }
            });

        } else {
            // We can use the cached URL.
            // TODO: screenWidth should be the current screen width.
            String imageUrl = GooglePlace.makeImageUrl(imageReference, screenWidth);
            Glide.with(context).load(imageUrl).into(holder.binding.ivBackdrop);
        }
    }

    public void addAll(Collection<? extends Journey> collection) {
        int insertionIndex = items.size();
        items.addAll(collection);
        notifyItemRangeInserted(insertionIndex, collection.size());
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    public void setOnJourneySelectedListener(OnItemSelectedListener listener) {
        this.listener = listener;
    }

    private void selectItemAtPosition(int position) {
        if (listener != null) {
            Journey journey = items.get(position);
            listener.onItemSelected(journey);
        }
    }
}
