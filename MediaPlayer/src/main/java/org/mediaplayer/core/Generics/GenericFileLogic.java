package org.mediaplayer.core.Generics;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mediaplayer.core.App;
import org.mediaplayer.core.Playlist;
import org.mediaplayer.core.Session;
import org.mediaplayer.core.TrackEntry;

import Tools.Files.FileManager;
import Tools.Files.Data.DataTypes.DataString;

public abstract class GenericFileLogic {
	public Map<String, TrackEntry> soundtracks;
	public Map<String, Playlist> playlists;
	public Map<String, Session> sessions;
	
	public GenericFileLogic() {
		soundtracks = new HashMap<>();
		playlists = new HashMap<>();
		sessions = new HashMap<>();
	}
	
	public abstract void create(App app) throws IOException;
	
	public abstract void createPlaylist(Path path, List<String> ids, List<String> subIds) throws IOException;
	public abstract Session createSession(List<Playlist> sessionParts, Path path, boolean looping, boolean shuffle) throws IOException;
	
	public abstract void loadSoundtracks() throws IOException;
	public abstract void loadPlaylists() throws IOException;
	public abstract void loadSessions() throws IOException;
	
	public abstract TrackEntry readEntry(TrackEntry entry, FileManager fm);
	public abstract Playlist readPlaylist(FileManager fm, Path path, String id);
	public abstract Session readSession(FileManager fm, Path path, String id);
	
	public abstract DataString getCurrentSessionData();
	
	public abstract void createIDsForUnknownSoundtracks() throws IOException;
	public abstract void save() throws IOException;
}
