package org.mediaplayer.core.Generics;

public abstract class GenericUI {
	public abstract void resetAll();
	public abstract void onSoundtrackChange(String id);
	public abstract void onPlaylistChange(String id);
	public abstract void onSessionChange(String id);
}
