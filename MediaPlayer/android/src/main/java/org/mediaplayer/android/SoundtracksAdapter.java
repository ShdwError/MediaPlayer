package org.mediaplayer.android;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.util.Consumer;
import androidx.recyclerview.widget.RecyclerView;

import org.mediaplayer.core.TrackEntry;

import java.util.List;
import java.util.Map;

public class SoundtracksAdapter extends RecyclerView.Adapter<SoundtracksAdapter.ViewHolder> {
    private List<TrackEntry> list;
    private Consumer<TrackEntry> onClick;

    public SoundtracksAdapter(List<TrackEntry> list, Consumer<TrackEntry> onClick) {
        this.list = list;
        this.onClick = onClick;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.soundtracks_item, viewGroup, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TrackEntry entry = list.get(position);

        holder.textView.setText(entry.getName());
        holder.itemView.setOnClickListener(v -> onClick.accept(entry));
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ViewHolder(View v) {
            super(v);
            textView = v.findViewById(R.id.textView);
        }
    }
}
