package org.mediaplayer.core.connection.messages;

import java.util.HashMap;
import java.util.Map;

import org.mediaplayer.core.Playlist;
import org.mediaplayer.core.Session;
import org.mediaplayer.core.TrackEntry;
import org.mediaplayer.core.connection.dtos.PlaylistDTO;
import org.mediaplayer.core.connection.dtos.SessionDTO;
import org.mediaplayer.core.connection.dtos.TrackEntryDTO;

import Tools.Core.Files.Data.DataTypes.DataBoolean;
import Tools.Core.Files.Data.DataTypes.DataInt;
import Tools.Core.Files.Data.DataTypes.DataMap;
import Tools.Core.Files.Data.DataTypes.DataString;

public class Sync extends GenericMessage {
	private Map<DataString, TrackEntryDTO> soundtracks;
	private Map<DataString, PlaylistDTO> playlists;
	private Map<DataString, SessionDTO> sessions;
	private DataInt version;
	private DataBoolean fullSync;
	public Sync() {
		setup(new HashMap<>(), new HashMap<>(), new HashMap<>(), new DataInt(), new DataBoolean());
	}
	public Sync(boolean fullSync) {
		setup(new HashMap<>(), new HashMap<>(), new HashMap<>(), new DataInt(), new DataBoolean(fullSync));
	}
	public Sync(int version) {
		setup(new HashMap<>(), new HashMap<>(), new HashMap<>(), new DataInt(version), new DataBoolean());
		created = true;
	}
	public Sync(Map<DataString, TrackEntryDTO> soundtracks, Map<DataString, PlaylistDTO> playlists,Map<DataString, SessionDTO> sessions, DataInt version, DataBoolean fullSync) {
		setup(soundtracks, playlists, sessions, version, fullSync);
		created = true;
	}
	private void setup(Map<DataString, TrackEntryDTO> soundtracks, Map<DataString, PlaylistDTO> playlists,Map<DataString, SessionDTO> sessions, DataInt version, DataBoolean fullSync) {
		this.soundtracks = soundtracks;
		this.playlists = playlists;
		this.sessions = sessions;
		this.version = version;
		this.fullSync = fullSync;
		createData(new DataMap<DataString, TrackEntryDTO>(DataString::new, TrackEntryDTO::new, soundtracks),
				new DataMap<DataString, PlaylistDTO>(DataString::new, PlaylistDTO::new, playlists),
				new DataMap<DataString, SessionDTO>(DataString::new, SessionDTO::new, sessions),
				version, fullSync);
	}
	public Map<DataString, TrackEntryDTO> getSoundtracks() {
		return soundtracks;
	}
	public void addTrackEntry(TrackEntry entry) {
		soundtracks.put(new DataString(entry.id), new TrackEntryDTO(entry));
		created = true;
	}
	
	public Map<DataString, PlaylistDTO> getPlaylists() {
		return playlists;
	}
	public void addPlaylist(Playlist playlist) {
		playlists.put(new DataString(playlist.id), new PlaylistDTO(playlist));
		created = true;
	}
	
	public Map<DataString, SessionDTO> getSessions() {
		return sessions;
	}
	public void addSession(Session session) {
		sessions.put(new DataString(session.id), new SessionDTO(session));
		created = true;
	}
	
	public boolean isEmpty() {
		return soundtracks.isEmpty() && playlists.isEmpty() && sessions.isEmpty();
	}
	public boolean isFullSync() {
		return fullSync.get();
	}
	
	public int getVersion() {
		return version.get();
	}
}
