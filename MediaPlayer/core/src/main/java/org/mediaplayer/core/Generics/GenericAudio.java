package org.mediaplayer.core.Generics;

import java.nio.file.Path;
import java.time.LocalDateTime;

import org.mediaplayer.core.DataPlaylistEntry;
import org.mediaplayer.core.Session;
import org.mediaplayer.core.TrackEntry;
import org.mediaplayer.core.Interfaces.AudioController;

public abstract class GenericAudio implements AudioController {

	protected Session currentSession;
	protected TrackEntry currentTrack;

	protected Path path;
	protected GenericFileLogic fileLogic;

	
	public abstract void playSoundtrack(TrackEntry entry);
	public void playSession(Session session) {
		if(session == null)
			return;
		session.lastOpened.set(LocalDateTime.now());

		currentSession = session;
		fileLogic.onPlaySession(session);
		DataPlaylistEntry dpe = session.getCurrent();
		if(dpe == null) {
			closePlayer();
			return;
		}
		TrackEntry entry = fileLogic.soundtracks.get(dpe.id.get());
		if(entry != null) {
			playAndSetNext(entry);
			onPlayTrack(entry);
		}
		else {
			onError("Soundtrack " + dpe.id + " not found");
			playNextTrack();
		}
	}
	public void playNextTrack() {
		currentSession.lastOpened.set(LocalDateTime.now());

		if(!hasPlayer() || currentSession == null) return;

		// TODO: Loop Amount
		DataPlaylistEntry dpe = currentSession.getNext();

		if(dpe == null) {
			fileLogic.deleteSession(currentSession);
			stopSession();
			onPlaylistEnd();
			return;
		}

		TrackEntry entry = fileLogic.soundtracks.get(dpe.id.get());
		if(entry != null) {
			playEntry(entry);
		}
		else {
			onError("Soundtrack " + dpe.id + " not found");
			playNextTrack();
		}
	}

	public void moveToSessionPos(int pos) {
		currentSession.lastOpened.set(LocalDateTime.now());

		if(!hasPlayer() || currentSession == null) return;

		DataPlaylistEntry dpe = currentSession.moveTo(pos);
		if(dpe == null) {
			System.out.println("Out of Bounds");
			return;
		}

		TrackEntry entry = fileLogic.soundtracks.get(dpe.id.get());
		if(entry != null) {
			playEntry(entry);
		}
		else {
			onError("Soundtrack " + dpe.id + " not found");
			playNextTrack();
		}
	}
	public void playPreviousTrack() {
		currentSession.lastOpened.set(LocalDateTime.now());

		if(!hasPlayer()) return;
		// TODO: Loop Amount
		DataPlaylistEntry dpe = currentSession.getPrevious();

		if(dpe == null) {
			onError("Beginning of Playlist");
			return;
		}

		TrackEntry entry = fileLogic.soundtracks.get(dpe.id.get());
		if(entry != null) {
			playEntry(entry);
		}
		else {
			onError("Soundtrack " + dpe.id + " not found");
			playPreviousTrack();
		}
	}
	private void playEntry(TrackEntry entry) {
		playAndSetNext(entry);
		onPlayTrack(entry);
		fileLogic.onPlayEntry(currentSession, entry);
	}
	public void reset() {
		boolean wasPaused = isPaused();
		Session newSession = null;
		if(currentSession != null)
			newSession = fileLogic.sessions.get(currentSession.id);
		playSession(newSession);
		if(wasPaused)
			pause();
	}
	public abstract void playAndSetNext(TrackEntry entry);
	
	public abstract void stopSession();
	public abstract void closePlayer();
	
	public abstract void setVolume(double v);
	public abstract double getVolume();
	public abstract void play();
	public abstract void pause();
	public abstract boolean isPaused();

	public Session getCurrentSession() {
		return currentSession;
	}
	public TrackEntry getCurrentTrack() {
		return currentTrack;
	}
	public boolean isCurrentTrack(TrackEntry entry) {
		return entry.id.equals(getCurrentTrack().id) && hasPlayer();
	}
	
	public abstract boolean hasPlayer();

	public abstract void setMediaLength(TrackEntry entry);

	public abstract void onError(String error);
	public abstract void onPlayTrack(TrackEntry entry);
	public abstract void onPlaylistEnd();
}
