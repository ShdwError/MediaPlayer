package org.mediaplayer.desktop;

import java.nio.file.Path;
import java.time.LocalDateTime;

import org.mediaplayer.core.Generics.GenericFileLogic;
import org.mediaplayer.core.Session;
import org.mediaplayer.core.TrackEntry;
import org.mediaplayer.core.UtilFunctions;
import org.mediaplayer.core.DataTypes.DataPlaylistEntry;
import org.mediaplayer.core.Generics.GenericAudio;

import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class DesktopAudio extends GenericAudio {
	private static MediaPlayer mediaPlayer;
	private ConsoleUI ui;

	private double volume;
	
	public DesktopAudio(Path path) {
		super();
		
		this.path = path;
		
		this.volume = 1;
	}

	public void create(GenericFileLogic fileLogic, ConsoleUI ui) {
		this.fileLogic = fileLogic;
		this.ui = ui;
	}
	
	//Play functions
	public void playSoundtrack(TrackEntry entry) {
		currentTrack = entry;
		
		Media media = new Media(path.resolve(entry.path).toUri().toString());
		if(mediaPlayer != null) {
			mediaPlayer.setOnEndOfMedia(null);
			mediaPlayer.stop();
			mediaPlayer.dispose();
		}
		
		mediaPlayer = new MediaPlayer(media);
		mediaPlayer.setVolume(volume);
		mediaPlayer.setAutoPlay(true);
		mediaPlayer.setOnError(() -> System.out.println("MediaPlayer Error: " + mediaPlayer.getError()));
	    media.setOnError(() -> System.out.println("Media Error: " + media.getError()));
		mediaPlayer.setOnReady(() -> {
			int length = (int) media.getDuration().toSeconds();
			entry.length.set(length);
		    System.out.println("Playtime: " + UtilFunctions.getLengthString(length));
		    mediaPlayer.play();
		});
		
		System.out.println("-------------");
		System.out.println("Playing: " + entry.getName());
		
	}
	public void playAndSetNext(TrackEntry entry) {
		playSoundtrack(entry);
		
		mediaPlayer.setOnEndOfMedia(() -> {
		    Platform.runLater(this::playNextTrack);
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

		mediaPlayer.setOnEndOfMedia(null);
		mediaPlayer.stop();
		mediaPlayer.dispose();
		mediaPlayer = null;
	}

	@Override
	public void setVolume(double v) {
		this.volume = v;
		if(mediaPlayer != null)
			mediaPlayer.setVolume(v);
	}
	@Override
	public void play() {
		if(mediaPlayer != null)
			mediaPlayer.play();
	}
	@Override
	public void pause() {
		if(mediaPlayer != null)
			mediaPlayer.pause();
	}

	@Override
	public boolean hasPlayer() {
		return mediaPlayer != null;
	}

	@Override
	public void setMediaLength(TrackEntry entry) {
		Media media = new Media(path.resolve(entry.path).toUri().toString());
		MediaPlayer player = new MediaPlayer(media);
		player.setOnReady(() -> {
			int sec = (int) media.getDuration().toSeconds();
			entry.length.set(sec);
			player.dispose();
		});
	}

	@Override
	public void onError(String error) {
		System.out.println(error);
	}

	@Override
	public void onPlayTrack(TrackEntry entry) {
		System.out.println("Play Track " + currentSession.pos.get());

		ui.printPlaylistPreview();
	}

	@Override
	public void onPlaylistEnd() {
		System.out.println("End of Playlist");
	}


}
