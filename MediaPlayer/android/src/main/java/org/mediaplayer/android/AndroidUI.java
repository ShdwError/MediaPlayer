package org.mediaplayer.android;

import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import org.mediaplayer.core.Playlist;
import org.mediaplayer.core.Session;
import org.mediaplayer.core.TrackEntry;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AndroidUI implements
        PlaylistCreationDialogFragment.OnPlaylistCreationClick,
        SidebarAdapter.OnClick {
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

    private RecyclerView sessionRecycler;

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

        sidebarAdapter = new SidebarAdapter(items, this, app);

        sidebar.setAdapter(sidebarAdapter);
    }

    private void createSessionsItem() {
        sessions.items.clear();
        sessions.items.add(new SidebarItem("+", "add_session", false));
        for(Session session: fileLogic.sessions.values()) {
            sessions.items.add(new SidebarItem(session.getName(), session.id, false));
        }
    }
    private void createPlaylistsItem() {
        playlists.items.clear();
        playlists.items.add(new SidebarItem("+", "add_playlist", false));
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

        //clean up
        sessionRecycler = null;
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
        ImageView play = view.findViewById(R.id.play_playlist);
        ImageView delete = view.findViewById(R.id.delete_playlist);

        title.setText(playlist.getName());
        size.setText("Size: " + playlist.getAll(fileLogic.playlists, false).size());
        length.setText("Playlength: " + audio.formatTime(fileLogic.playlistLength(playlist)));

        play.setOnClickListener(v -> {

        });
        delete.setOnClickListener(v -> {
            fileLogic.deletePlaylist(playlist);
            playlistAdapter = null;
            subplaylistsAdapter = null;
            frame.removeAllViews();
            sidebarAdapter.delete(playlists, id);
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

        //clean up
        sessionRecycler = null;
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

        ImageView play = view.findViewById(R.id.play_session);
        TextView shuffle = view.findViewById(R.id.shuffle_session);
        TextView loop = view.findViewById(R.id.loop_session);
        ImageView delete = view.findViewById(R.id.delete_session);

        title.setText(session.getName());
        size.setText("Size: " + session.size());
        length.setText("Playlength: " + audio.formatTime(fileLogic.playlistLength(session)));

        play.setOnClickListener(v -> {
            audio.playSession(session);
        });

        //Entries
        this.sessionRecycler = view.findViewById(R.id.session_tracks);
        sessionRecycler.setLayoutManager(new LinearLayoutManager(app.getApplicationContext()));

        playlistAdapter = new PlaylistAdapter(session, fileLogic, audio, (entry, pos) ->  {
            if(audio.getCurrentSession() != session)
                audio.playSession(session);
            audio.moveToSessionPos(pos);
            playlistAdapter.notifyItemChanged(pos);
        });

        sessionRecycler.setAdapter(playlistAdapter);

        if(audio.getCurrentSession() == session) {

        }
    }

    public void createNewPlaylistPopup() {
        List<PlaylistCreationSearchItem> items = new ArrayList<>();
        for(TrackEntry trackEntry: fileLogic.tracksSortedByName()) {
            items.add(new PlaylistCreationSearchItem("s", trackEntry.getName(),
                    audio.formatTime(trackEntry.length.get()), trackEntry.id));
        }
        for(Playlist playlist: fileLogic.playlistsSortedByName()) {
            items.add(new PlaylistCreationSearchItem("p", playlist.getName(),
                    "" + playlist.size(), playlist.id));
        }

        PlaylistCreationDialogFragment dialog = new PlaylistCreationDialogFragment(app,
                items, this);
        dialog.show(app.getSupportFragmentManager(), "Create Playlist");
    }

    public void onSoundtrackEnd(TrackEntry entry) {
        playlistAdapter.onSoundtrackEnd(entry);
    }
    public void onSessionTrack(TrackEntry entry) {
        int pos = audio.getCurrentSession().pos.get();
        playlistAdapter.notifyItemChanged(pos);

        if(sessionRecycler != null) {
            LinearLayoutManager lm = (LinearLayoutManager) sessionRecycler.getLayoutManager();
            lm.scrollToPositionWithOffset(pos, 500);
        }
    }

    public void moveToPlaylistPos(int pos) {

    }

    @Override
    public void onSidebarClick(SidebarItem item) {
        if(item.id.equals("add_playlist")) {
            createNewPlaylistPopup();
        }
        else if(item.id.equals("soundtracks")) {
            currentSidebarItem = item;
            showSoundtracks();
        }
        else if(fileLogic.playlists.containsKey(item.id)) {
            currentSidebarItem = item;
            showPlaylist(item.id);
        }
        else if(fileLogic.sessions.containsKey(item.id)) {
            currentSidebarItem = item;
            showSession(item.id);
        }
    }
    @Override
    public void onPlaylistCreationAttempt(String name, String path, List<PlaylistCreationSearchItem> items) {
        Log.println(Log.ASSERT, "Test", name + ", " + path + "," + items);
        path = path.replace("\\", "/");

        Path completePath = Path.of("Playlists", path, name + ".txt");
        List<String> entriesToAdd = new ArrayList<>();
        List<String> playlistsToAdd = new ArrayList<>();

        for(PlaylistCreationSearchItem item: items) {
            if(item.type.equals("s")) {
                entriesToAdd.add(item.id);
            }
            else if(item.type.equals("p")) {
                playlistsToAdd.add(item.id);
            }
            else {
                Log.println(Log.ASSERT, "Test", "Wrong type?");
            }
        }
        try {
            Playlist playlist = fileLogic.createPlaylist(completePath, entriesToAdd, playlistsToAdd);
            int pos = playlists.addSorted(new SidebarItem(playlist.getName(), playlist.id, false));
            sidebarAdapter.expand(playlists);

            LinearLayoutManager lm = (LinearLayoutManager) sidebar.getLayoutManager();
            lm.scrollToPositionWithOffset(pos, 500);

            showPlaylist(playlist.id);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
