package org.mediaplayer.android.ui;

import androidx.fragment.app.DialogFragment;

import java.util.List;

public class EntryOptionsDialogFragment extends DialogFragment {
    private List<EntryOptionsItem> items;
    public EntryOptionsDialogFragment(List<EntryOptionsItem> items) {
        this.items = items;
    }

    @Override
    public void onStart() {
        super.onStart();

        if(getDialog() != null) {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.5);
            int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.5);

            getDialog().getWindow().setLayout(width, height);
            //getDialog().getWindow().setPo
        }
    }
}
