package org.mediaplayer.server;

import org.mediaplayer.core.Session;
import org.mediaplayer.core.TrackEntry;
import org.mediaplayer.core.Interfaces.AudioController;

public class AudioMock implements AudioController {

	@Override
	public void setMediaLength(TrackEntry entry) {
	}
	@Override
	public boolean isCurrentTrack(TrackEntry entry) {
		return false;
	}
	@Override
	public void closePlayer() {
	}

	@Override
	public void playAndSetNext(TrackEntry entry) {
	}

	@Override
	public void playNextTrack() {
	}

	@Override
	public Session getCurrentSession() {
		return null;
	}
	@Override
	public void stopSession() {
	}

}
