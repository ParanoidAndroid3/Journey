package com.paranoidandroid.journey.journeys.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.paranoidandroid.journey.R;
import com.paranoidandroid.journey.databinding.ItemJourneyBinding;
import com.paranoidandroid.journey.models.Destination;
import com.paranoidandroid.journey.models.Journey;
import com.paranoidandroid.journey.models.Leg;
import com.paranoidandroid.journey.support.DateFormattingUtils;
import com.paranoidandroid.journey.support.GooglePlaceInfo;

import java.util.Collection;
import java.util.List;

public class JourneysAdapter extends RecyclerView.Adapter<JourneysAdapter.ViewHolder> {
    private static final String TAG = "JourneysAdapter";
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

    public JourneysAdapter(List<Journey> items) {
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

        Destination destination = journey.getLegs().get(0).getDestination();
        double lat = destination.getLatitude();
        double lng = destination.getLongitude();
        String imageReference = destination.getGoogleImageReference();

        // TODO: screenWidth should be the current screen width.
        String imageUrl = new GooglePlaceInfo(imageReference, lat, lng).getImageUrl(screenWidth);

        SimpleTarget<GlideDrawable> target = new BackdropTarget(
                holder.binding.ivBackdrop, holder.binding.ivScrim);
        Glide.with(context).load(imageUrl).into(target);
    }

    public void add(int position, Journey journey) {
        items.add(position, journey);
        notifyItemInserted(position);
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

    public Journey get(int position) {
        return items.get(position);
    }

    public int indexOf(String itemId) {
        for (int i = 0; i < items.size(); i++) {
            Journey j = items.get(i);
            if (j.getObjectId().equals(itemId))
                return i;
        }
        return -1;
    }

    public Journey remove(int position) {
        Journey oldValue = items.remove(position);
        notifyItemRemoved(position);
        return oldValue;
    }

    public int size() {
        return items.size();
    }

    public List<Journey> getAll() {
        return items;
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

    /**
     * Loads the scrim when an image finishes loading an image.
     */
    private static class BackdropTarget extends SimpleTarget<GlideDrawable> {
        ImageView backdrop;
        ImageView scrim;
        Animation fadeIn = new AlphaAnimation(0.0f, 1.0f);

        BackdropTarget(ImageView backdrop, ImageView scrim) {
            this.backdrop = backdrop;
            this.scrim = scrim;
            fadeIn.setDuration(500);
            scrim.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onResourceReady(GlideDrawable resource,
                GlideAnimation<? super GlideDrawable> glideAnimation) {
            backdrop.setImageDrawable(resource.getCurrent());
            backdrop.startAnimation(fadeIn);
            scrim.startAnimation(fadeIn);
            scrim.setVisibility(View.VISIBLE);
        }
    }
}
