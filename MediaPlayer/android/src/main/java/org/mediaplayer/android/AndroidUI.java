package org.mediaplayer.android;

import android.content.Context;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.mediaplayer.core.Playlist;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AndroidUI implements SidebarAdapter.OnItemClick {
    private AndroidFileLogic fileLogic;
    private AndroidAudio audio;
    private AppCompatActivity app;


    private DrawerLayout drawer;

    private RecyclerView sidebar;
    private SidebarAdapter sidebarAdapter;

    private SidebarItem curSession;
    private SidebarItem sessions;
    private SidebarItem playlists;
    private SidebarItem soundtracks;

    public AndroidUI(AppCompatActivity app) {
        this.app = app;
    }
    public void create(AndroidAudio audio, AndroidFileLogic fileLogic) {
        this.audio = audio;
        this.fileLogic = fileLogic;
    }
    public void start() {
        app.setContentView(R.layout.activity_main);

        sidebar = app.findViewById(R.id.sidebar);
        sidebar.setLayoutManager(new LinearLayoutManager(app));

        createPlaylistsItem();
        createSessionsItem();
        createCurSessionItem();
        createSoundtracksItem();

        List<SidebarItem> items = new ArrayList<>();
        items.add(sessions);
        items.add(playlists);
        items.add(soundtracks);

        sidebarAdapter = new SidebarAdapter(items, this);
    }
    private void createCurSessionItem() {
        //Placeholder
        this.curSession = new SidebarItem("Current Session", "cur_ses", true);
    }
    private void createSessionsItem() {
        this.sessions = new SidebarItem("Sessions", "sessions", true);
    }
    private void createPlaylistsItem() {
        this.playlists = new SidebarItem("Playlists", "playlists", true);

        for(Playlist playlist: fileLogic.playlistsSortedByName()) {
            playlists.items.add(new SidebarItem(playlist.getName(), playlist.id, false));
        }
    }
    private void createSoundtracksItem() {
        this.soundtracks = new SidebarItem("Soundtracks", "soundtracks", true);
    }

    @Override
    public void onClick(SidebarItem item) {
        FrameLayout frame = app.findViewById(R.id.content_frame);
        frame.removeAllViews();

        if(item.id.equals("sessions")) {

        }
        else if(item.id.equals("soundtracks")) {

        }
        else if(fileLogic.playlists.containsKey(item.id)) {

        }
    }
}
