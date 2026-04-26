package org.mediaplayer.android;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.mediaplayer.core.Playlist;
import org.mediaplayer.core.Session;
import org.mediaplayer.core.TrackEntry;

import java.nio.file.Path;
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

    private PlaylistAdapter playlistAdapter;
    private SubplaylistsAdapter subplaylistsAdapter;

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
    public void showPlaylist(String id) {
        FrameLayout frame = app.findViewById(R.id.content_frame);
        frame.removeAllViews();

        View view = app.getLayoutInflater().inflate(R.layout.playlist_content, null);
        frame.addView(view);

        //Header
        Playlist playlist = fileLogic.playlists.get(id);

        TextView title = view.findViewById(R.id.playlist_title);
        TextView size = view.findViewById(R.id.playlist_size);
        TextView length = view.findViewById(R.id.playlist_length);
        TextView play = view.findViewById(R.id.play_playlist);

        title.setText(playlist.getName());
        size.setText("Size: " + playlist.getAll(fileLogic.playlists, false).size());
        length.setText("Playlength: " + audio.formatTime(fileLogic.playlistLength(playlist)));

        play.setOnClickListener(v -> {
            playPlaylist(id);
        });

        //Entries
        RecyclerView entries = view.findViewById(R.id.playlist_tracks);
        entries.setLayoutManager(new LinearLayoutManager(app.getApplicationContext()));

        playlistAdapter = new PlaylistAdapter(playlist, fileLogic, audio, (entry, pos) ->  {
            audio.playSingleSoundtrack(entry);
        });

        entries.setAdapter(playlistAdapter);

        //Subplaylists
        RecyclerView subplaylists = view.findViewById(R.id.playlist_subplaylists);
        subplaylists.setLayoutManager(new LinearLayoutManager(app.getApplicationContext()));

        subplaylistsAdapter = new SubplaylistsAdapter(playlist, fileLogic, this, audio);

        subplaylists.setAdapter(subplaylistsAdapter);
    }

    public void showSession(String id) {
        FrameLayout frame = app.findViewById(R.id.content_frame);
        frame.removeAllViews();

        View view = app.getLayoutInflater().inflate(R.layout.session_content, null);
        frame.addView(view);

        //Header
        Session session = fileLogic.sessions.get(id);

        TextView title = view.findViewById(R.id.playlist_title);
        TextView size = view.findViewById(R.id.playlist_size);
        TextView length = view.findViewById(R.id.playlist_length);

        TextView play = view.findViewById(R.id.play_session);
        TextView shuffle = view.findViewById(R.id.shuffle_session);
        TextView loop = view.findViewById(R.id.loop_session);

        title.setText(session.getName());
        size.setText("Size: " + session.size());
        length.setText("Playlength: " + audio.formatTime(fileLogic.playlistLength(session)));

        play.setOnClickListener(v -> {
            audio.playSession(session);
        });

        //Entries
        RecyclerView entries = view.findViewById(R.id.session_tracks);
        entries.setLayoutManager(new LinearLayoutManager(app.getApplicationContext()));

        playlistAdapter = new PlaylistAdapter(session, fileLogic, audio, (entry, pos) ->  {
            if(audio.getCurrentSession() != session)
                audio.playSession(session);
            audio.moveToSessionPos(pos);
            playlistAdapter.notifyItemChanged(pos);
        });

        entries.setAdapter(playlistAdapter);
    }

    public void playPlaylist(String playlistID) {
        //TODO later Pop-UP

        List<Playlist> list = new ArrayList<>();
        list.add(fileLogic.playlists.get(playlistID));

        //Path path = Path.of()

        //fileLogic.createSession()
    }

    public void onSoundtrackEnd(TrackEntry entry) {
        playlistAdapter.onSoundtrackEnd(entry);
    }
    public void onSessionTrack(TrackEntry entry) {
        playlistAdapter.notifyItemChanged(audio.getCurrentSession().pos.get());
    }



    @Override
    public void onClick(SidebarItem item) {
        currentSidebarItem = item;
        Log.println(Log.ASSERT, "Test", item.id);

        if(item.id.equals("soundtracks")) {
            showSoundtracks();
        }
        else if(fileLogic.playlists.containsKey(item.id)) {
            showPlaylist(item.id);
        }
        else if(fileLogic.sessions.containsKey(item.id)) {
            showSession(item.id);
        }
    }
}
