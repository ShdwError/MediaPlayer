package org.mediaplayer.android;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import org.mediaplayer.core.DataTypes.DataPlaylistEntry;
import org.mediaplayer.core.Generics.GenericAudio;
import org.mediaplayer.core.Generics.GenericFileLogic;
import org.mediaplayer.core.Session;
import org.mediaplayer.core.TrackEntry;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Locale;

public class AndroidAudio extends GenericAudio {
    private MediaPlayer mediaPlayer;
    private Context context;

    private float volume;
    private boolean running;

    private AndroidUI ui;
    public AndroidAudio(Context context, Path path) {
        super();

        this.path = path;
        this.context = context;
        this.volume = 1;
        this.running = false;

        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);


        this.mediaPlayer = new MediaPlayer();
    }

    public void create(GenericFileLogic fileLogic, AndroidUI ui) {
        this.fileLogic = fileLogic;
        this.ui = ui;
    }

    public void playSingleSoundtrack(TrackEntry entry)  {
        playSoundtrack(entry);
        mediaPlayer.setOnCompletionListener((mp) -> {
            running = false;
            onTrackEnd(entry);
        });
    }

    @Override
    public void playSoundtrack(TrackEntry entry) {
        currentTrack = entry;
        running = true;

        closePlayer();
        mediaPlayer = new MediaPlayer();

        File file = new File(context.getExternalFilesDir(null), path.resolve(entry.path).toString());
        try {
            mediaPlayer.setDataSource(file.getAbsolutePath());
        } catch (IOException e) {
            //TODO: Error Message
            throw new RuntimeException(e);
        }
        //mediaPlayer.setVolume(volume, volume);

        mediaPlayer.setOnPreparedListener(MediaPlayer::start);
        mediaPlayer.prepareAsync();
    }
    @Override
    public void playAndSetNext(TrackEntry entry) {
        playSoundtrack(entry);

        mediaPlayer.setOnCompletionListener((mp) -> {
            onTrackEnd(entry);
            playNextTrack();
        });
    }
    @Override
    public void stopSession() {
        currentSession = null;
        fileLogic.getCurrentSessionData().created = false;

        closePlayer();
    }
    @Override
    public void closePlayer() {
        if(mediaPlayer == null) return;

        mediaPlayer.release();
        mediaPlayer = null;
    }
    @Override
    public void setVolume(double v) {
        this.volume = (float) v;

        if(mediaPlayer != null)
            mediaPlayer.setVolume(this.volume, this.volume);
    }

    @Override
    public double getVolume() {
        return this.volume;
    }

    @Override
    public void play() {
        if(mediaPlayer != null) {
            mediaPlayer.start();
            this.running = true;
        }
    }
    @Override
    public void pause() {
        if(mediaPlayer != null) {
            mediaPlayer.pause();
            this.running = false;
        }
    }
    public boolean switchRunning() {
        if(isRunning()) pause();
        else play();
        return running;
    }
    public boolean isRunning() {
        return running;
    }

    @Override
    public boolean hasPlayer() {
        return mediaPlayer != null;
    }
    @Override
    public void setMediaLength(TrackEntry entry) {
        File file = new File(context.getExternalFilesDir(null), path.resolve(entry.path).toString());
        if(!file.exists()) {
            onError("Could not open " + entry);
            return;
        }
        MediaPlayer player = new MediaPlayer();
        try {
            player.setDataSource(file.getAbsolutePath());
        } catch (IOException e) {
            //TODO: Error Message
            throw new RuntimeException(e);
        }
        player.setOnPreparedListener(mp -> {
            int sec = player.getDuration()/1000;
            entry.length.set(sec);
            Log.i("Test", entry.getName() + ":" + sec);
            player.release();
        });
        player.prepareAsync();
    }
    @Override
    public void onError(String error) {
        Log.e("Error", error);
    }

    @Override
    public void onPlayTrack(TrackEntry entry) {
        ui.onSessionTrack(entry);

        if(currentSession.dataContainer != null) {
            try {
                currentSession.dataContainer.system.save();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void onTrackEnd(TrackEntry entry) {
        ui.onSoundtrackEnd(entry);
    }

    @Override
    public void onPlaylistEnd() {
    }

    public String formatTime(int sec) {
        int s = sec % 60;
        sec /= 60;
        int m = sec % 60;
        sec /= 60;
        int h = sec;

        if(h != 0) return h + ":" + String.format(Locale.getDefault(), "%02d", m) + ":" + String.format(Locale.getDefault(), "%02d", s);
        else return m + ":" + String.format(Locale.getDefault(), "%02d", s);
    }


}
