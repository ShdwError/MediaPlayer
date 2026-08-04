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

import Tools.Files.Data.Exceptions.DataTypeException;

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
            this.audio.create(fileLogic, ui);
            this.ui.create(audio, fileLogic);
        } catch (IOException | DataTypeException e) {
            throw new RuntimeException(e);
        }
        Log.println(Log.ASSERT, "Test", "Finish");

        this.ui.start();
    }
    public void save(){
        try {
            fileLogic.save();
        }
        catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        save();
    }
    @Override
    protected void onPause() {
        super.onPause();
        save();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(isFinishing())
            audio.closePlayer();
    }
}
