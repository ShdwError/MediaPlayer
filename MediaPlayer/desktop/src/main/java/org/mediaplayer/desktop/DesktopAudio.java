package org.mediaplayer.desktop;

import java.nio.file.Path;
import java.time.LocalDateTime;

import org.mediaplayer.core.App;
import org.mediaplayer.core.Session;
import org.mediaplayer.core.TrackEntry;
import org.mediaplayer.core.UtilFunctions;
import org.mediaplayer.core.DataTypes.DataPlaylistEntry;
import org.mediaplayer.core.Generics.GenericAudio;

import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class DesktopAudio extends GenericAudio {
	private Session currentSession;
	private TrackEntry currentTrack;
	
	private static MediaPlayer mediaPlayer;
	
	private Path path;
	private DesktopFileLogic fileLogic;
	private ConsoleUI ui;
	
	private double volume;
	
	public DesktopAudio(Path path) {
		super();
		
		this.path = path;
		
		this.volume = 1;
	}
	
	@Override
	public void create(App app) {
		this.fileLogic = (DesktopFileLogic) app.fileLogic;
		this.ui = (ConsoleUI) app.ui;
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
	public void playSession(Session session) {
		session.lastOpened.set(LocalDateTime.now());
		
		currentSession = session;
		fileLogic.getCurrentSessionData().set(session.id);
		DataPlaylistEntry dpe = session.getCurrent();
		if(dpe == null) {
			return;
		}
		TrackEntry entry = fileLogic.soundtracks.get(dpe.id.get());
		if(entry != null) {
			playSoundtrack(entry);
			
			mediaPlayer.setOnEndOfMedia(() -> {
			    Platform.runLater(this::playNextTrack);
			});
		}
		else {
			System.out.println("Soundtrack " + dpe.id + " not found");
			playNextTrack();
		}
		
	}
	public void playNextTrack() {
		currentSession.lastOpened.set(LocalDateTime.now());
		
		if(mediaPlayer == null || currentSession == null) return;
			
		// TODO: Loop Amount
		DataPlaylistEntry dpe = currentSession.getNext();
		
		if(dpe == null) {
			fileLogic.deleteSession(currentSession);
			System.out.println("End of Playlist");
			return;
		}
		
		System.out.println("Play next Track " + currentSession.pos.get());
		
		ui.printPlaylistPreview();
		
		TrackEntry entry = fileLogic.soundtracks.get(dpe.id.get());
		if(entry != null) {
			playSoundtrack(entry);
			
			mediaPlayer.setOnEndOfMedia(() -> {
			    Platform.runLater(this::playNextTrack);
			});
		}
		else {
			System.out.println("Soundtrack " + dpe.id + " not found");
			playNextTrack();
		}
	}
	public void moveToSessionPos(int pos) {
		currentSession.lastOpened.set(LocalDateTime.now());
		
		if(mediaPlayer == null || currentSession == null) return;
		
		DataPlaylistEntry dpe = currentSession.moveTo(pos);
		
		if(dpe == null) {
			System.out.println("Out of Bounds");
			return;
		}
		System.out.println("Play Track " + pos);
		
		ui.printPlaylistPreview();
		
		TrackEntry entry = fileLogic.soundtracks.get(dpe.id.get());
		if(entry != null) {
			playAndSetNext(entry);
		}
		else {
			System.out.println("Soundtrack " + dpe.id + " not found");
			playNextTrack();
		}
		
	}
	public void playAndSetNext(TrackEntry entry) {
		playSoundtrack(entry);
		
		mediaPlayer.setOnEndOfMedia(() -> {
		    Platform.runLater(this::playNextTrack);
		});
	}
	public void playPreviousTrack() {
		currentSession.lastOpened.set(LocalDateTime.now());
		
		if(mediaPlayer == null) return;
		// TODO: Loop Amount
		DataPlaylistEntry dpe = currentSession.getPrevious();
		
		if(dpe == null) {
			System.out.println("Beginning of Playlist");
			return;
		}
		
		System.out.println("Playing Track " + currentSession.pos.get());
		
		ui.printPlaylistPreview();
		
		TrackEntry entry = fileLogic.soundtracks.get(dpe.id.get());
		if(entry != null) {
			playAndSetNext(entry);
		}
		else {
			System.out.println("Soundtrack " + dpe.id + " not found");
			playPreviousTrack();
		}
	}
	public void stopSession() {
		if(mediaPlayer == null) return;
		currentSession = null;
		fileLogic.getCurrentSessionData().created = false;
		
		mediaPlayer.setOnEndOfMedia(null);
		mediaPlayer.stop();
		mediaPlayer.dispose();
		mediaPlayer = null;
	}
	public void closePlayer() {
		mediaPlayer.setOnEndOfMedia(null);
		mediaPlayer.stop();
		mediaPlayer.dispose();
		mediaPlayer = null;
	}
	
	public void setVolume(double v) {
		this.volume = v;
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
	public Session getCurrentSession() {
		return currentSession;
	}
	@Override
	public TrackEntry getCurrentTrack() {
		return currentTrack;
	}
	@Override
	public boolean hasPlayer() {
		return mediaPlayer != null;
	}
}
