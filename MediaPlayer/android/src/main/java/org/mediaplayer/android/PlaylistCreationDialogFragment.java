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
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PlaylistCreationDialogFragment extends DialogFragment {
    private EditText editName, editPath;
    private RecyclerView itemsSelected;
    private SearchView itemSearch;
    private RecyclerView itemSearchList;
    private TextView done, back;

    private PlaylistCreationSearchAdapter selectedAdapter;
    private PlaylistCreationSearchAdapter searchAdapter;

    private OnPlaylistCreationClick listener;
    private String nameDefault, pathDefault;

    private List<PlaylistCreationSearchItem> items;
    private List<PlaylistCreationSearchItem> defaultItems;
    private AppCompatActivity app;

    public PlaylistCreationDialogFragment(AppCompatActivity app, List<PlaylistCreationSearchItem> items,
            OnPlaylistCreationClick listener) {
        this.app = app;
        this.items = items;
        this.listener = listener;
        this.defaultItems = new ArrayList<>();
    }
    public PlaylistCreationDialogFragment(AppCompatActivity app, List<PlaylistCreationSearchItem> items,
                                          OnPlaylistCreationClick listener, String nameDefault,
                                          String pathDefault, List<PlaylistCreationSearchItem> defaultItems) {
        this.app = app;
        this.items = items;
        this.listener = listener;
        this.nameDefault = nameDefault;
        this.pathDefault = pathDefault;
        this.defaultItems = defaultItems;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getDialog() != null) {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
            int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.8);

            getDialog().getWindow().setLayout(width, height);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.create_playlist_content, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.editName = view.findViewById(R.id.playlist_name);
        if(this.nameDefault != null)
            this.editName.setText(this.nameDefault);

        this.editPath = view.findViewById(R.id.playlist_path);
        if(this.pathDefault != null)
            this.editPath.setText(this.pathDefault);

        this.itemsSelected = view.findViewById(R.id.playlist_items_selected);
        this.itemsSelected.setLayoutManager(new LinearLayoutManager(app.getApplicationContext()));
        this.selectedAdapter = new PlaylistCreationSearchAdapter(defaultItems, (item) -> {
            searchAdapter.addItemSorted(item);
            selectedAdapter.removeItem(item);
        });
        this.itemsSelected.setAdapter(this.selectedAdapter);

        this.itemSearchList = view.findViewById(R.id.playlist_item_search_list);
        this.itemSearchList.setLayoutManager(new LinearLayoutManager(app.getApplicationContext()));
        this.searchAdapter = new PlaylistCreationSearchAdapter(items, (item) -> {
            selectedAdapter.addItem(item);
            searchAdapter.removeItem(item);
        });
        this.itemSearchList.setAdapter(searchAdapter);

        this.itemSearch = view.findViewById(R.id.playlist_item_search);
        this.itemSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                searchAdapter.filterBy(newText);
                return false;
            }
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchAdapter.filterBy(query);
                return false;
            }
        });

        this.back = view.findViewById(R.id.back);
        this.back.setOnClickListener((v) -> {
            dismiss();
        });
        this.done = view.findViewById(R.id.done);
        this.done.setOnClickListener((v) -> {
            String name = editName.getText().toString();
            if(name.isEmpty()) return;

            String path = editPath.getText().toString();
            List<PlaylistCreationSearchItem> items = selectedAdapter.getItems();

            listener.onPlaylistCreationAttempt(name, path, items);
            dismiss();
        });
    }

    public interface OnPlaylistCreationClick {
        void onPlaylistCreationAttempt(String name, String path,
                                       List<PlaylistCreationSearchItem> items);
    }
}


