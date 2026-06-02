package org.mediaplayer.android.ui;

import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SidebarAdapter extends RecyclerView.Adapter<SidebarAdapter.ViewHolder> {

    private List<SidebarItem> visibleItems;
    public List<SidebarItem> rootItems;

    private OnClick listener;

    public SidebarAdapter(List<SidebarItem> rootItems, OnClick listener, AppCompatActivity app) {
        this.rootItems = rootItems;
        this.visibleItems = new ArrayList<>();
        this.listener = listener;
        rebuildList();
    }
    private void rebuildList() {
        visibleItems.clear();
        for(SidebarItem item : rootItems) {
            visibleItems.add(item);
            if(item.isHeader && item.isExpanded) {
                visibleItems.addAll(item.items);
            }
        }
        notifyDataSetChanged();
    }
    public void expand(SidebarItem item) {
        item.isExpanded = true;
        rebuildList();
    }
    public void delete(SidebarItem rootItem, String id) {
        for(SidebarItem item: rootItem.items) {
            if(item.id.equals(id)) {
                rootItem.items.remove(item);
                break;
            }
        }
        rebuildList();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView textView = new TextView(parent.getContext());
        textView.setPadding(40, 30, 40, 30);
        textView.setTextSize(16);
        return new ViewHolder(textView);
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SidebarItem item = visibleItems.get(position);

        holder.textView.setText(item.title);
        if(item.isHeader) {
            holder.textView.setPadding(40, 30, 40, 30);
            holder.textView.setTextSize(16);
        }
        else {
            holder.textView.setPadding(80, 30, 40, 30);
            holder.textView.setTextSize(14);
        }

        holder.textView.setOnClickListener(v -> {
            if(item.isHeader) {
                item.isExpanded = !item.isExpanded;
                rebuildList();
            }
            listener.onSidebarClick(item);
        });
    }
    @Override
    public int getItemCount() {
        return visibleItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ViewHolder(TextView itemView) {
            super(itemView);
            textView = itemView;
        }
    }
    public interface OnClick {
        void onSidebarClick(SidebarItem item);
    }

}
