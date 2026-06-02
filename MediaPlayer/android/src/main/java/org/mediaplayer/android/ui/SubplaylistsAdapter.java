package org.mediaplayer.android.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.mediaplayer.android.AndroidAudio;
import org.mediaplayer.android.AndroidFileLogic;
import org.mediaplayer.android.AndroidUI;
import org.mediaplayer.android.R;
import org.mediaplayer.core.Playlist;

public class SubplaylistsAdapter extends RecyclerView.Adapter<SubplaylistsAdapter.ViewHolder> {
    private Playlist playlist;
    private AndroidFileLogic fileLogic;
    private AndroidUI ui;
    private AndroidAudio audio;

    public SubplaylistsAdapter(Playlist playlist, AndroidFileLogic fileLogic, AndroidUI ui, AndroidAudio audio) {
        this.playlist = playlist;
        this.fileLogic = fileLogic;
        this.ui = ui;
        this.audio = audio;
    }

    @Override
    public SubplaylistsAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.subplaylists_item, viewGroup, false);
        return new SubplaylistsAdapter.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int pos) {
        Playlist subplaylist = fileLogic.playlists.get(playlist.getSubPlaylists().get(pos).get());
        holder.title.setText(subplaylist.getName());
        holder.size.setText("Size: " + subplaylist.getAll(fileLogic.playlists, false).size());
        holder.length.setText("Playlength: " + audio.formatTime(fileLogic.playlistLength(subplaylist)));

        holder.itemView.setOnClickListener(v -> {
            ui.showPlaylist(subplaylist.id);
        });
    }
    @Override
    public int getItemCount() {
        return playlist.getSubPlaylists().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, size, length;

        public ViewHolder(View view) {
            super(view);
            this.title = view.findViewById(R.id.subplaylist_title);
            this.size = view.findViewById(R.id.subplaylist_size);
            this.length = view.findViewById(R.id.subplaylist_length);
        }
    }


}
