package org.mediaplayer.android.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.mediaplayer.android.R;

import java.util.ArrayList;
import java.util.List;

public class CreationSearchAdapter
        extends RecyclerView.Adapter<CreationSearchAdapter.ViewHolder> {
    private List<CreationSearchItem> items;
    private List<CreationSearchItem> visible;

    private OnItemClick listener;

    private String filter;
    private boolean showHidden;
    public CreationSearchAdapter(List<CreationSearchItem> items, OnItemClick listener, boolean showHidden) {
        this.items = items;
        this.visible = items;

        this.filter = "";
        this.showHidden = showHidden;

        this.listener = listener;
    }

    public void filterBy(String filter) {
        this.filter = filter.toLowerCase();
        filter();
    }
    public void filter() {
        visible = new ArrayList<>();
        for(CreationSearchItem item : items) {
            if((showHidden || item.visible) &&
                    item.name.toLowerCase().contains(filter)) {
                visible.add(item);
            }
        }

        notifyDataSetChanged();
    }
    public void addItem(CreationSearchItem item) {
        items.add(item);
        filterBy(this.filter);
    }
    public void removeItem(CreationSearchItem item) {
        items.remove(item);
        filterBy(this.filter);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.create_searchitem, viewGroup, false);
        return new CreationSearchAdapter.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int pos) {
        CreationSearchItem item = visible.get(pos);

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

    public List<CreationSearchItem> getItems() {
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
        void onClick(CreationSearchItem item);
    }
}
