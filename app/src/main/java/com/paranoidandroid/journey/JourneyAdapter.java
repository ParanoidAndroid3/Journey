package com.paranoidandroid.journey;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.paranoidandroid.journey.databinding.ItemJourneyBinding;
import com.paranoidandroid.journey.models.Journey;

import java.util.Collection;
import java.util.List;

public class JourneyAdapter extends RecyclerView.Adapter<JourneyAdapter.ViewHolder> {

    class ViewHolder extends RecyclerView.ViewHolder {
        ItemJourneyBinding binding;

        ViewHolder(ItemJourneyBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    private List<Journey> items;

    public JourneyAdapter(List<Journey> items) {
        this.items = items;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemJourneyBinding binding = ItemJourneyBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Journey journey = items.get(position);
        holder.binding.setJourney(journey);
        holder.binding.executePendingBindings();
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
}
