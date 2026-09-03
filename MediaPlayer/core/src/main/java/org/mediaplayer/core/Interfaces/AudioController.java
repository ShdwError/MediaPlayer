package org.mediaplayer.core.Interfaces;

import org.mediaplayer.core.Session;
import org.mediaplayer.core.TrackEntry;

public interface AudioController {
	void setMediaLength(TrackEntry entry);
	boolean isCurrentTrack(TrackEntry entry);
	void closePlayer();
	void playAndSetNext(TrackEntry entry);
	void playNextTrack();
	Session getCurrentSession();
	void stopSession();
}
