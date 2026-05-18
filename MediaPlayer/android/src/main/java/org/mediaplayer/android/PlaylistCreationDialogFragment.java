package org.mediaplayer.android;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PlaylistCreationDialogFragment extends CreationDialogFragment {

    private OnPlaylistCreationClick listener;
    public PlaylistCreationDialogFragment(List<CreationSearchItem> items,
            OnPlaylistCreationClick listener) {
        this.items = items;
        this.listener = listener;
        this.defaultItems = new ArrayList<>();
    }
    public PlaylistCreationDialogFragment(List<CreationSearchItem> items,
                                          OnPlaylistCreationClick listener, String nameDefault,
                                          String pathDefault, List<CreationSearchItem> defaultItems) {
        this.items = items;
        this.listener = listener;
        this.nameDefault = nameDefault;
        this.pathDefault = pathDefault;
        this.defaultItems = defaultItems;
    }

    @Override
    public void onDoneClick(String name, String path, List<CreationSearchItem> items) {
        listener.onPlaylistCreationAttempt(name, path, items);
    }

    public interface OnPlaylistCreationClick {
        void onPlaylistCreationAttempt(String name, String path,
                                       List<CreationSearchItem> items);
    }
}


