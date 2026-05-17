package org.mediaplayer.android;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.mediaplayer.core.DataTypes.DataPlaylistEntry;
import org.mediaplayer.core.Playlist;
import org.mediaplayer.core.TrackEntry;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {
    private Playlist playlist;
    private AndroidFileLogic fileLogic;
    private AndroidAudio audio;

    private int currentlyRunning;

    private OnEntryClick listener;

    public PlaylistAdapter(Playlist playlist, AndroidFileLogic fileLogic, AndroidAudio audio,
                           OnEntryClick listener) {
        this.playlist = playlist;
        this.fileLogic = fileLogic;
        this.audio = audio;

        this.currentlyRunning = -1;

        this.listener = listener;
    }

    @NonNull
    @Override
    public PlaylistAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.playlist_item, viewGroup, false);
        return new PlaylistAdapter.ViewHolder(view);
    }

    @Override
    @SuppressLint("RecyclerView")
    public void onBindViewHolder(@NonNull ViewHolder holder, int pos) {
        DataPlaylistEntry dpe = playlist.get(pos);
        TrackEntry entry = fileLogic.soundtracks.get(dpe.id.get());
        if(entry == null) {
            //TODO Error
            return;
        }

        holder.number.setText(String.valueOf(pos));
        holder.name.setText(entry.getName());
        holder.length.setText(audio.formatTime(entry.length.get()));

        if(audio.isRunning() && audio.getCurrentTrack() == entry) {
            currentlyRunning = pos;
            holder.play.setImageResource(R.drawable.pause_button);
        }
        else {
            holder.play.setImageResource(R.drawable.play_arrow);
        }

        holder.play.setOnClickListener(v -> {
            if(audio.getCurrentTrack() == entry) {
                audio.switchRunning();
            }
            else {
                if(audio.isRunning()) {
                    notifyItemChanged(currentlyRunning);
                }
                listener.onClick(entry, holder.getAbsoluteAdapterPosition());
            }
            this.currentlyRunning = holder.getAbsoluteAdapterPosition();
            notifyItemChanged(currentlyRunning);
        });

        holder.menu.setOnClickListener(v -> {
            // TODO PopupMenu
        });
    }

    @Override
    public int getItemCount() {
        return playlist.size();
    }

    public void onSoundtrackEnd(TrackEntry entry) {
        if(currentlyRunning >= 0 && currentlyRunning < playlist.size() &&
                entry.id.equals(playlist.get(currentlyRunning).id.get())) {
            notifyItemChanged(currentlyRunning);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView number, name, length;
        ImageView play;
        TextView menu;

        public ViewHolder(View view) {
            super(view);
            this.number = view.findViewById(R.id.track_number);
            this.name = view.findViewById(R.id.track_name);
            this.length = view.findViewById(R.id.track_length);
            this.play = view.findViewById(R.id.track_play);
            this.menu = view.findViewById(R.id.track_menu);
        }
    }

    public interface OnEntryClick {
        void onClick(TrackEntry entry, int pos);
    }
}
