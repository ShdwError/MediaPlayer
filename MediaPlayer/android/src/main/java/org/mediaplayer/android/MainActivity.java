package org.mediaplayer.android;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import org.mediaplayer.core.Playlist;
import org.mediaplayer.core.TrackEntry;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private AndroidFileLogic fileLogic;
    private AndroidAudio audio;
    private AndroidUI ui;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            Path path = Path.of("MediaPlayer").toAbsolutePath();
            this.fileLogic = new AndroidFileLogic(this, path);
            this.audio = new AndroidAudio(this, path);
            this.ui = new AndroidUI(this);

            this.fileLogic.create(audio);
            this.audio.create(fileLogic);
            this.ui.create(audio, fileLogic);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Log.println(Log.ASSERT, "Test", "Finish");

        this.ui.start();

        setContentView(R.layout.activity_main);

        sidebar = findViewById(R.id.sidebar);
        sidebar.setLayoutManager(new LinearLayoutManager(this));

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);

                FrameLayout frame = findViewById(R.id.content_frame);
                frame.removeAllViews();

                int id = item.getItemId();
                if(id == R.id.nav_current_session) {

                }
                else if(id == R.id.nav_sessions) {

                }
                else if(id == R.id.nav_playlists) {
                    Menu menu = navigationView.getMenu();
                    SubMenu subMenu = item.getSubMenu();
                    subMenu.clear();

                    for(Playlist playlist : fileLogic.playlistsSortedByName()) {
                        subMenu.add(
                                Menu.NONE,
                                Menu.NONE,
                                Menu.NONE,
                                playlist.getName()
                        );
                    }
                }
                else if(id == R.id.nav_soundtracks) {
                    View view = getLayoutInflater().inflate(R.layout.soundtracks_content, null);
                    frame.addView(view);

                    RecyclerView recycler = view.findViewById(R.id.soundtracks_list);
                    recycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                    SoundtracksAdapter adapter = new SoundtracksAdapter(fileLogic.tracksSortedByName(), entry -> {
                        audio.playSoundtrack(entry);
                    });

                    recycler.setAdapter(adapter);
                }

                return true;
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            fileLogic.save();
            audio.closePlayer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
