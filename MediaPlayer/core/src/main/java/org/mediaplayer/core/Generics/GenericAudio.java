package org.mediaplayer.core.Generics;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;

import org.mediaplayer.core.DataTypes.DataPlaylistEntry;
import org.mediaplayer.core.Session;
import org.mediaplayer.core.TrackEntry;

public abstract class GenericAudio {

	protected Session currentSession;
	protected TrackEntry currentTrack;

	protected Path path;
	protected GenericFileLogic fileLogic;

	
	public abstract void playSoundtrack(TrackEntry entry);
	public void playSession(Session session) {
		session.lastOpened.set(LocalDateTime.now());

		currentSession = session;
		fileLogic.getCurrentSessionData().set(session.id);
		DataPlaylistEntry dpe = session.getCurrent();
		if(dpe == null) {
			closePlayer();
			return;
		}
		TrackEntry entry = fileLogic.soundtracks.get(dpe.id.get());
		if(entry != null) {
			playAndSetNext(entry);
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
		onPlayTrack(entry);

		if(entry != null) {
			playAndSetNext(entry);
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
		onPlayTrack(entry);

		if(entry != null) {
			playAndSetNext(entry);
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
		onPlayTrack(entry);

		if(entry != null) {
			playAndSetNext(entry);
		}
		else {
			onError("Soundtrack " + dpe.id + " not found");
			playPreviousTrack();
		}
	}
	public abstract void playAndSetNext(TrackEntry entry);
	
	public abstract void stopSession();
	public abstract void closePlayer();
	
	public abstract void setVolume(double v);
	public abstract void play();
	public abstract void pause();

	public Session getCurrentSession() {
		return currentSession;
	}
	public TrackEntry getCurrentTrack() {
		return currentTrack;
	}
	
	public abstract boolean hasPlayer();

	public abstract void setMediaLength(TrackEntry entry);

	public abstract void onError(String error);
	public abstract void onPlayTrack(TrackEntry entry);
	public abstract void onPlaylistEnd();
}
