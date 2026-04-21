package org.mediaplayer.android;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.mediaplayer.core.Playlist;
import org.mediaplayer.core.Session;

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

    private SidebarItem currentSidebarItem;

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

        this.sessions = new SidebarItem("Sessions", "sessions", true);
        this.playlists = new SidebarItem("Playlists", "playlists", true);
        this.soundtracks = new SidebarItem("Soundtracks", "soundtracks", true);

        createPlaylistsItem();
        createSessionsItem();

        List<SidebarItem> items = new ArrayList<>();
        items.add(sessions);
        items.add(playlists);
        items.add(soundtracks);

        sidebarAdapter = new SidebarAdapter(items, this);

        sidebar.setAdapter(sidebarAdapter);
    }

    private void createSessionsItem() {
        sessions.items.clear();
        for(Session session: fileLogic.sessions.values()) {
            sessions.items.add(new SidebarItem(session.getName(), session.id, false));
        }
    }
    private void createPlaylistsItem() {
        playlists.items.clear();
        for(Playlist playlist: fileLogic.playlistsSortedByName()) {
            playlists.items.add(new SidebarItem(playlist.getName(), playlist.id, false));
        }
    }

    public void showSoundtracks() {
        FrameLayout frame = app.findViewById(R.id.content_frame);
        frame.removeAllViews();

        View view = app.getLayoutInflater().inflate(R.layout.soundtracks_content, null);
        frame.addView(view);

        RecyclerView recycler = view.findViewById(R.id.soundtracks_list);
        recycler.setLayoutManager(new LinearLayoutManager(app.getApplicationContext()));

        SoundtracksAdapter adapter = new SoundtracksAdapter(fileLogic.tracksSortedByName(), entry -> {
            audio.playSoundtrack(entry);
        });

        recycler.setAdapter(adapter);
    }



    @Override
    public void onClick(SidebarItem item) {
        currentSidebarItem = item;
        Log.println(Log.ASSERT, "Test", item.id);

        if(item.id.equals("sessions")) {

        }
        else if(item.id.equals("soundtracks")) {
            showSoundtracks();
        }
        else if(fileLogic.playlists.containsKey(item.id)) {

        }
    }
}
