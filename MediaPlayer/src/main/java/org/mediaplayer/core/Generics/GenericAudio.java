package org.mediaplayer.core.Generics;

import java.io.IOException;

import org.mediaplayer.core.App;
import org.mediaplayer.core.Session;
import org.mediaplayer.core.TrackEntry;

public abstract class GenericAudio {
	public abstract void create(App app) throws IOException;
	
	public abstract void playSoundtrack(TrackEntry entry);
	public abstract void playSession(Session session);
	public abstract void playNextTrack();
	
	public abstract void moveToSessionPos(int pos);
	public abstract void playAndSetNext(TrackEntry entry);
	public abstract void playPreviousTrack();
	
	public abstract void stopSession();
	public abstract void closePlayer();
	
	public abstract void setVolume(double v);
	public abstract void play();
	public abstract void pause();
	
	public abstract Session getCurrentSession();
	
	
	public abstract TrackEntry getCurrentTrack();
	
	public abstract boolean hasPlayer();
}
