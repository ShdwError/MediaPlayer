package org.mediaplayer.android;


import org.mediaplayer.core.Session;

import java.util.ArrayList;
import java.util.List;

public class SessionCreationDialogFragment extends CreationDialogFragment {
    private OnSessionCreationClick listener;
    public SessionCreationDialogFragment(List<CreationSearchItem> items,
                                         OnSessionCreationClick listener) {
        this.items = items;
        this.listener = listener;
        this.defaultItems = new ArrayList<>();
    }
    public SessionCreationDialogFragment(List<CreationSearchItem> items,
                                          OnSessionCreationClick listener, String nameDefault,
                                          String pathDefault, List<CreationSearchItem> defaultItems) {
        this.items = items;
        this.listener = listener;
        this.nameDefault = nameDefault;
        this.pathDefault = pathDefault;
        this.defaultItems = defaultItems;
    }
    @Override
    public void onDoneClick(String name, String path, List<CreationSearchItem> items) {
        listener.onSessionCreationAttempt(name, path, items);
    }

    public interface OnSessionCreationClick {
        void onSessionCreationAttempt(String name, String path,
                                       List<CreationSearchItem> items);
    }
}
