package org.mediaplayer.android.ui;


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

import org.mediaplayer.android.R;

import java.util.ArrayList;
import java.util.List;

public class SessionCreationDialogFragment extends CreationDialogFragment {
    private OnSessionCreationClick listener;
    public SessionCreationDialogFragment(List<CreationSearchItem> items,
                                         OnSessionCreationClick listener) {
        super(items, null, null, new ArrayList<>());
    }
    public SessionCreationDialogFragment(List<CreationSearchItem> items,
                                          OnSessionCreationClick listener, String nameDefault,
                                          String pathDefault, List<CreationSearchItem> defaultItems) {
        super(items, nameDefault, pathDefault, defaultItems);
        this.listener = listener;
    }

    @Override
    public void onCreationAttempt(String name, String path, List<CreationSearchItem> items) {
        listener.onSessionCreationAttempt(name, path, items);
    }

    public interface OnSessionCreationClick {
        void onSessionCreationAttempt(String name, String path,
                                       List<CreationSearchItem> items);
    }
}
