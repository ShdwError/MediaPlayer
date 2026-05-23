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

public class PlaylistCreationDialogFragment extends DialogFragment {

    private EditText editName, editPath;
    private RecyclerView itemsSelected;
    private SearchView itemSearch;
    private RecyclerView itemSearchList;
    private TextView done, back;

    private CreationSearchAdapter selectedAdapter;
    private CreationSearchAdapter searchAdapter;
    private String nameDefault, pathDefault;

    private List<CreationSearchItem> items;
    private List<CreationSearchItem> defaultItems;

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

        this.editName = view.findViewById(R.id.name);
        if (this.nameDefault != null)
            this.editName.setText(this.nameDefault);

        this.editPath = view.findViewById(R.id.path);
        if (this.pathDefault != null)
            this.editPath.setText(this.pathDefault);

        this.itemsSelected = view.findViewById(R.id.items_selected);
        this.itemsSelected.setLayoutManager(new LinearLayoutManager(requireContext()));
        this.selectedAdapter = new CreationSearchAdapter(defaultItems, (item) -> {
            item.visible = true;
            searchAdapter.filter();
            selectedAdapter.removeItem(item);
        }, true);
        this.itemsSelected.setAdapter(this.selectedAdapter);

        this.itemSearchList = view.findViewById(R.id.item_search_list);
        this.itemSearchList.setLayoutManager(new LinearLayoutManager(requireContext()));
        this.searchAdapter = new CreationSearchAdapter(items, (item) -> {
            selectedAdapter.addItem(item);
            item.visible = false;
            searchAdapter.filter();
        }, false);
        this.itemSearchList.setAdapter(searchAdapter);

        this.itemSearch = view.findViewById(R.id.item_search);
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
            if (name.isEmpty()) return;

            String path = editPath.getText().toString();
            List<CreationSearchItem> items = selectedAdapter.getItems();

            listener.onPlaylistCreationAttempt(name, path, items);
            dismiss();
        });
    }

    public interface OnPlaylistCreationClick {
        void onPlaylistCreationAttempt(String name, String path,
                                       List<CreationSearchItem> items);
    }
}


