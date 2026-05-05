package org.mediaplayer.android;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SidebarAdapter extends RecyclerView.Adapter<SidebarAdapter.ViewHolder> {

    public List<SidebarItem> rootItems;
    private AppCompatActivity app;

    private OnClick listener;

    public SidebarAdapter(List<SidebarItem> rootItems, OnClick listener, AppCompatActivity app) {
        this.rootItems = rootItems;
        this.listener = listener;
        this.app = app;
    }
    @Override
    public SidebarAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.sidebar_item, viewGroup, false);
        return new SidebarAdapter.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int pos) {
        SidebarItem item = rootItems.get(pos);

        holder.header.setText(item.title);
        if(holder.subitems_adapter == null) {
            holder.subitems_adapter = new SidebarAdapter(item.items, listener, app);
            holder.subitems.setAdapter(holder.subitems_adapter);
        }

        if(item.isHeader) {
            holder.header.setPadding(40, 30, 40, 30);
            holder.header.setTextSize(16);
        }
        else {
            holder.header.setPadding(80, 30, 40, 30);
            holder.header.setTextSize(14);
        }

        holder.header.setOnClickListener(v -> {
            item.isExpanded = !item.isExpanded;
            if(item.isExpanded) {
                holder.subitems.setVisibility(View.VISIBLE);
                if(item.hasAdded)
                    holder.add_subitem.setVisibility(View.VISIBLE);
            }
            else {
                holder.subitems.setVisibility(View.GONE);
                holder.add_subitem.setVisibility(View.GONE);
            }
            listener.onSidebarClick(item);
        });

        holder.add_subitem.setOnClickListener(v -> {
            listener.onAddClick(item);
        });
    }
    @Override
    public int getItemCount() {
        return rootItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView header;
        RecyclerView subitems;
        TextView add_subitem;
        SidebarAdapter subitems_adapter;

        public ViewHolder(View view) {
            super(view);

            this.header = view.findViewById(R.id.sidebar_header);
            this.subitems = view.findViewById(R.id.sidebar_subitems);
            this.subitems.setLayoutManager(new LinearLayoutManager(app.getApplicationContext()));
            this.add_subitem = view.findViewById(R.id.sidebar_add_subitem);
        }
    }

    public interface OnClick {
        void onSidebarClick(SidebarItem item);
        void onAddClick(SidebarItem item);
    }

}
