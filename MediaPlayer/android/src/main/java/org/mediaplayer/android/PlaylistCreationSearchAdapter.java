package org.mediaplayer.android;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PlaylistCreationSearchAdapter
        extends RecyclerView.Adapter<PlaylistCreationSearchAdapter.ViewHolder> {
    private List<PlaylistCreationSearchItem> items;
    private List<PlaylistCreationSearchItem> visible;

    private OnItemClick listener;

    private String filter;
    public PlaylistCreationSearchAdapter(List<PlaylistCreationSearchItem> items, OnItemClick listener) {
        this.items = items;
        this.visible = items;

        this.filter = "";

        this.listener = listener;
    }

    public void filterBy(String filter) {
        this.filter = filter;
        if(filter.isEmpty()) {
            visible = items;
        }
        else {
            visible = new ArrayList<>();
            filter = filter.toLowerCase();
            for(PlaylistCreationSearchItem item : items) {
                if(item.name.toLowerCase().contains(filter)) {
                    visible.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }
    public void addItemSorted(PlaylistCreationSearchItem toAdd) {
        boolean added = false;
        for(int i = 0; i < items.size(); i++) {
            if(toAdd.name.compareTo(items.get(i).name) <= 0) {
                items.add(i, toAdd);
                added = true;
                break;
            }
        }
        if(!added) items.add(toAdd);
        filterBy(this.filter);

        notifyDataSetChanged();
    }
    public void addItem(PlaylistCreationSearchItem item) {
        items.add(item);
        filterBy(this.filter);

        notifyDataSetChanged();
    }
    public void removeItem(PlaylistCreationSearchItem item) {
        items.remove(item);
        filterBy(this.filter);

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.create_playlist_searchitem, viewGroup, false);
        return new PlaylistCreationSearchAdapter.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int pos) {
        PlaylistCreationSearchItem item = visible.get(pos);

        holder.item_name.setText(item.name);
        holder.item_type.setText(item.type);
        holder.item_length.setText(String.valueOf(item.length));

        holder.layout.setOnClickListener((v) -> {
            if(listener != null) listener.onClick(item);
        });
    }
    @Override
    public int getItemCount() {
        return visible.size();
    }

    public List<PlaylistCreationSearchItem> getItems() {
        return items;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView item_type, item_name, item_length, item_menu;
        LinearLayout layout;

        public ViewHolder(View view) {
            super(view);
            this.item_type = view.findViewById(R.id.item_type);
            this.item_name = view.findViewById(R.id.item_name);
            this.item_length = view.findViewById(R.id.item_length);
            this.item_menu = view.findViewById(R.id.item_menu);
            this.layout = view.findViewById(R.id.layout);
        }
    }
    public interface OnItemClick {
        void onClick(PlaylistCreationSearchItem item);
    }
}
