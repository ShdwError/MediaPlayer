package org.mediaplayer.core.Generics;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.mediaplayer.core.Data.PlaylistsData;
import org.mediaplayer.core.Data.SessionsData;
import org.mediaplayer.core.Data.SoundtracksData;
import org.mediaplayer.core.Interfaces.AudioController;

import Tools.Core.Files.DataSystem;
import Tools.Core.Files.GenericFileManager;
import Tools.Core.Files.GenericFileTree;
import Tools.Core.Files.Util;
import Tools.Core.Files.Data.DataContainer;
import Tools.Core.Files.Data.DataTypes.DataArray;
import Tools.Core.Files.Data.DataTypes.DataBoolean;
import Tools.Core.Files.Data.DataTypes.DataDate;
import Tools.Core.Files.Data.DataTypes.DataInt;
import Tools.Core.Files.Data.DataTypes.DataMap;
import Tools.Core.Files.Data.DataTypes.DataString;
import Tools.Core.Files.Data.Exceptions.DataTypeException;
import Tools.Core.Util.NaturalOrderComparator;

import org.mediaplayer.core.DataPlaylistEntry;
import org.mediaplayer.core.Playlist;
import org.mediaplayer.core.Session;
import org.mediaplayer.core.TrackEntry;
import org.mediaplayer.core.UtilFunctions;

public abstract class GenericFileLogic {
	
	//TODO private
	public Map<String, TrackEntry> soundtracks;
	public Map<String, Playlist> playlists;
	public Map<String, Session> sessions;
	
	protected GenericFileTree fTree;
	protected Path path;
	protected AudioController audio;

	protected DataSystem<SoundtracksData> stDataSystem;
	protected SoundtracksData stData;
	protected DataSystem<PlaylistsData> plDataSystem;
	protected PlaylistsData plData;
	protected DataSystem<SessionsData> sDataSystem;
	protected SessionsData sData;

	private NaturalOrderComparator naturalOrderComparator;
	private final Object dataLock;
	
	public GenericFileLogic(Path path) throws IOException {
		soundtracks = new HashMap<>();
		playlists = new HashMap<>();
		sessions = new HashMap<>();

		this.path = path;

		this.naturalOrderComparator = new NaturalOrderComparator();
		this.dataLock = new Object();
	}
	public void create() throws IOException, DataTypeException {
		fTree = createFileTree();
		fTree.createFile(Path.of("Soundtracks.txt"));
		fTree.createFile(Path.of("Playlists.txt"));
		fTree.createFile(Path.of("Sessions.txt"));
		fTree.createFolder(Path.of("Soundtracks"));
		fTree.createFolder(Path.of("Playlists"));
		fTree.createFolder(Path.of("Sessions"));

		loadSoundtracks();
		loadPlaylists();
		loadSessions();

		createIDsForUnknownSoundtracks();
	}
	public abstract GenericFileTree createFileTree() throws IOException;
	
	//Load Functions
	public void loadSoundtracks() throws IOException, DataTypeException {
		synchronized(dataLock) {
			stDataSystem = new DataSystem<>(fTree.get(Path.of("Soundtracks.txt")), SoundtracksData::new);
			stDataSystem.read();
			stData = stDataSystem.getOrCreate();
	
			correctPathData(stData.soundtracks);
			stData.soundtracks.forEach((id, data) -> {
				Path cPath = Path.of(data.get());
				GenericFileManager infoFM = fTree.getOrCreate(UtilFunctions.getSoundtrackInfoPath(cPath));
				TrackEntry entry = readEntry(new TrackEntry(cPath, id.get()), infoFM);
				soundtracks.put(id.get(), entry);
			});
		}
	}
	public void loadPlaylists() throws IOException, DataTypeException {
		synchronized(dataLock) {
			plDataSystem = new DataSystem<>(fTree.get(Path.of("Playlists.txt")), PlaylistsData::new);
			plDataSystem.read();
			plData = plDataSystem.getOrCreate();
	
			correctPathData(plData.playlists);
			plData.playlists.forEach((id, data) -> {
				GenericFileManager fm = fTree.get(Path.of(data.get()));
				Playlist playlist = readPlaylist(fm, Path.of(data.get()), id.get());
				//if(playlist == null) plData.playlists.remove(id);
			});
		}
	}
	public void loadSessions() throws IOException, DataTypeException {
		synchronized(dataLock) {
			sDataSystem = new DataSystem<>(fTree.get(Path.of("Sessions.txt")), SessionsData::new);
			sDataSystem.read();
			sData = sDataSystem.getOrCreate();
	
			List<DataString> toRemove = new ArrayList<>();
	
			correctPathData(sData.sessions);
			sData.sessions.forEach((id, data) -> {
				GenericFileManager fm = fTree.get(Path.of(data.get()));
				Session session = readSession(fm, Path.of(data.get()), id.get());
				if(session == null) toRemove.add(id);
			});
	
			for(DataString id: toRemove)
				sData.sessions.remove(id);
		}
	}
	//Read Functions
	public TrackEntry readEntry(TrackEntry entry, GenericFileManager fm)  {
		synchronized(dataLock) {
			if(fm != null) {
				DataSystem<TrackEntry> ds = new DataSystem<>(fm, () -> entry);
	            try {
	                ds.read();
	            } catch (IOException | DataTypeException e) {
	                throw new RuntimeException(e);
	            }
				ds.createNewDataContainer("");
	
	            if(!entry.length.created) {
					audio.setMediaLength(entry);
				}
			}
			else {
				entry.length = new DataInt();
				audio.setMediaLength(entry);
			}
			return entry;
		}
	}

	public Playlist readPlaylist(GenericFileManager fm, Path path, String id) {
		synchronized(dataLock) {
			if(fm != null) {
				DataSystem<Playlist> ds = new DataSystem<>(fm, () -> new Playlist(path, id));
				try {
					ds.read();
				} catch (IOException | DataTypeException e) {
					e.printStackTrace();
				}
				Playlist playlist = ds.getOrCreate();
				if(playlist != null) {
					for(int i = 0; i < playlist.size(); i++) {
						TrackEntry entry = soundtracks.get(playlist.getId(i));
						if(entry != null) entry.inPlaylists.add(id);
					}
					playlist.reorganize();
					playlists.put(id, playlist);
				}
				return playlist;
			}
			return null;
		}
	}
	public Session readSession(GenericFileManager fm, Path path, String id)  {
		synchronized(dataLock) {
			if(fm != null) {
				if(sessions.containsKey(id)) return sessions.get(id);
				DataSystem<Session> ds = new DataSystem<>(fm, () -> new Session(path, id));
				try {
					ds.read();
				} catch (IOException | DataTypeException e) {
					e.printStackTrace();
				}
				Session session = ds.getOrCreate();
				if(session != null) {
					session.reorganize();
					sessions.put(id, session);
				}
				return session;
			}
			return null;
		}
	}
	//Create Functions
	public Playlist createPlaylist(Path path, List<String> ids, List<String> subIds) throws IOException {
		String id = UUID.randomUUID().toString();
		List<DataPlaylistEntry> entries = new ArrayList<>();
		for(String entryId: ids) {
			DataPlaylistEntry dpe = createEntry(id, entryId);
			if(dpe != null) entries.add(dpe);
		}
		return createPlaylist(path, id, entries, subIds);
	}
	public Playlist createPlaylist(Path path, String id, List<DataPlaylistEntry> entries, List<String> subIds) throws IOException {
		synchronized(dataLock) {
			plData.playlists.put(new DataString(id), new DataString(path.toString()));
			GenericFileManager fm = fTree.createFile(path);
			Playlist playlist = readPlaylist(fm, path, id);
			playlist.add(entries);
			for(String subPlaylistId: subIds) {
				playlist.addSubplaylist(subPlaylistId);
			}
			playlist.reorganize();
			playlist.dataSystem.save();
			plData.dataSystem.save();
			return playlist;
		}
	}
	public Session createSession(List<Playlist> sessionParts, List<String> entryIds, Path path, boolean looping, boolean shuffle) throws IOException {
		String id = UUID.randomUUID().toString();
		List<DataPlaylistEntry> entries = new ArrayList<>();
		for(String entryId: entryIds) {
			DataPlaylistEntry dpe = createEntry(id, entryId);
			if(dpe != null) entries.add(dpe);
		}
		for(Playlist playlist: sessionParts) {
			entries.addAll(playlist.getAll(playlists, true));
		}
		return createSession(path, id, entries, looping);
	}
	public Session createSession(Path path, String id, List<DataPlaylistEntry> entries, boolean looping) throws IOException {
		synchronized(dataLock) {
			sData.sessions.put(new DataString(id), new DataString(path.toString()));
			GenericFileManager fm = fTree.createFile(path);
			Session session = readSession(fm, path, id);
			session.add(entries);
			
			session.loop.set(looping);
			
			session.reorganize();
			
			session.dataSystem.save();
			sData.dataSystem.save();
			
			return session;
		}
	}
	private DataPlaylistEntry createEntry(String playlistId, String entryId) {
		TrackEntry entry = soundtracks.get(entryId);
		if(entry != null) {
			entry.inPlaylists.add(playlistId);
			return new DataPlaylistEntry(new DataString(entryId));
		}
		return null;
	}
	//Rename Functions
	public boolean renameSoundtrack(String entryId, Path newPath) {
		return renameSoundtrack(soundtracks.get(entryId), newPath);
	}
	public boolean renameSoundtrack(TrackEntry entry, Path newPath) {
		synchronized(dataLock) {
			try {
				boolean currentTrack = audio.isCurrentTrack(entry);
				if(currentTrack) {
					audio.closePlayer();
					System.gc();
				}
				newPath = fTree.getNextFreeFileName(newPath);
				Path oldPath = entry.path;
		
				entry.path = newPath;
				stData.soundtracks.put(new DataString(entry.id), new DataString(newPath.toString()));
				
				fTree.rename(oldPath, newPath);
				GenericFileManager newTrackManager = fTree.rename(UtilFunctions.getSoundtrackInfoPath(oldPath), UtilFunctions.getSoundtrackInfoPath(newPath));
				
				entry.dataSystem.changeFileManager(newTrackManager);
				stDataSystem.save();
				
				if(currentTrack) {
					audio.playAndSetNext(entry);
				}
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}
			return false;
		}
	}
	public boolean renamePlaylist(String playlistId, Path newPath) {
		return renamePlaylist(playlists.get(playlistId), newPath);
	}
	public boolean renamePlaylist(Playlist playlist, Path newPath) {
		synchronized(dataLock) {
			newPath = fTree.getNextFreeFileName(newPath);
			Path oldPath = playlist.path;
	
			try {
				playlist.dataSystem.changeFileManager(fTree.rename(oldPath, newPath));
				plDataSystem.save();
				
				playlist.path = newPath;
				plData.playlists.put(new DataString(playlist.id), new DataString(newPath.toString()));
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}
			return false;
		}
	}
	public boolean renameSession(String sessionId, Path newPath) {
		return renameSession(sessions.get(sessionId), newPath);
	}
	public boolean renameSession(Session session, Path newPath) {
		synchronized(dataLock) {
			newPath = fTree.getNextFreeFileName(newPath);
			Path oldPath = session.path;
	
			try {
				session.dataSystem.changeFileManager(fTree.rename(oldPath, newPath));
				sDataSystem.save();
				
				session.path = newPath;
				sData.sessions.put(new DataString(session.id), new DataString(newPath.toString()));
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}
			return false;
		}
	}
	public boolean deleteSoundtrack(String entryId) {
		return deleteSoundtrack(soundtracks.get(entryId));
	}
	public boolean deleteSoundtrack(TrackEntry entry) {
		synchronized(dataLock) {
			try {
				for(String id: entry.inPlaylists) {
					Playlist playlist = playlists.get(id);
					if(playlist != null)
						playlist.remove(new DataPlaylistEntry(new DataString(entry.id)));
					else
						System.out.println("Playlist " + id + " does not exist");
				}
				if(audio.isCurrentTrack(entry)) {
					audio.playNextTrack();
					System.gc();
				} 
				fTree.remove(entry.path);
				fTree.remove(UtilFunctions.getSoundtrackInfoPath(entry.path));
				stData.soundtracks.remove(new DataString(entry.id));
				soundtracks.remove(entry.id);
				stDataSystem.save();
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}
			return false;
		}
	}
	public boolean deletePlaylist(String playlistId) {
		return deletePlaylist(playlists.get(playlistId));
	}
	public boolean deletePlaylist(Playlist playlist) {
		synchronized(dataLock) {
			try {
				fTree.remove(playlist.path);
				plData.playlists.remove(new DataString(playlist.id));
				playlists.remove(playlist.id);
				plDataSystem.save();
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}
			return false;
		}
	}
	public boolean deleteSession(String sessionId) {
		return deleteSession(sessions.get(sessionId));
	}
	public boolean deleteSession(Session session) {
		synchronized(dataLock) {
			try {
				if(audio.getCurrentSession() == session) {
					audio.stopSession();
				}
				fTree.remove(session.path);
				sData.sessions.remove(new DataString(session.id));
				sessions.remove(session.id);
				sDataSystem.save();
				return true;
			} catch (IOException e) {
					e.printStackTrace();
			}
			return false;
		}
	}
	public boolean addSoundtrackTo(DataPlaylistEntry dpe, String playlistId) {
		return addSoundtrackTo(dpe, playlists.get(playlistId));
	}
	public boolean addSoundtrackTo(DataPlaylistEntry dpe, Playlist playlist) {
		synchronized(dataLock) {
			if(playlist == null) return false;
			TrackEntry entry = soundtracks.get(dpe.id.get());
			entry.inPlaylists.add(playlist.id);
			playlist.add(dpe);
			return true;
		}
	}
	public boolean removeSoundtrackFrom(String entryId, String playlistId) {
		return removeSoundtrackFrom(soundtracks.get(entryId), playlists.get(playlistId));
	}
	public boolean removeSoundtrackFrom(TrackEntry entry, Playlist playlist) {
		synchronized(dataLock) {
			if(playlist == null || !entry.inPlaylists.contains(playlist.id)) return false;
	
			entry.inPlaylists.remove(playlist.id);
			return playlist.remove(entry);
		}
	}
	public boolean linkEntryTo(String playlistId, String entryId, String linkedToId) {
		Playlist playlist = playlists.get(playlistId);
		return linkEntryTo(playlist, playlist.get(entryId), linkedToId);
	}
	public boolean linkEntryTo(Playlist playlist, DataPlaylistEntry dpe, String linkedToId) {
		synchronized(dataLock) {
			if(playlist == null || dpe == null || !soundtracks.containsKey(linkedToId) || dpe.id.get().equals(linkedToId)) return false;
			dpe.setForcedNext(linkedToId);
			return true;
		}
	}
	public boolean addSubplaylist(String playlistId, String subplaylistId) {
		return addSubplaylist(playlists.get(playlistId), subplaylistId);
	}
	public boolean addSubplaylist(Playlist playlist, String subplaylistId) {
		synchronized(dataLock) {
			if(playlist == null || subplaylistId == null || !playlists.containsKey(subplaylistId) || playlist.id.equals(subplaylistId)) 
				return false;
			playlist.addSubplaylist(subplaylistId);
			return true;
		}
	}
	public boolean removeSubplaylist(String playlistId, String subplaylistId) {
		return removeSubplaylist(playlists.get(playlistId), subplaylistId);
	}
	public boolean removeSubplaylist(Playlist playlist, String subplaylistId) {
		synchronized(dataLock) {
			if(playlist == null)
				return false;
			return playlist.removeSubplaylist(subplaylistId);
		}
	}
	public void shuffle(String sessionId, boolean keepActive) {
		shuffle(sessions.get(sessionId), keepActive);
	}
	public void shuffle(Session session, boolean keepActive) {
		synchronized(dataLock) {
			session.shuffle(keepActive);
		}
	}
	public boolean moveEntryTo(String playlistId, String fromId, int to) {
		synchronized(dataLock) {
			if(playlistId == null || fromId == null) return false;
			Playlist playlist = playlists.get(playlistId);
			if(playlist == null) return false;
			
			DataPlaylistEntry dpe = playlist.get(fromId);
			playlist.remove(dpe);
			playlist.addAt(to, dpe);
			return true;
		}
	}
	public boolean moveEntryTo(Playlist playlist, int from, int to) {
		synchronized(dataLock) {
			if(from < 0 || from >= playlist.size() || to < 0 || to >= playlist.size()) return false;
			DataPlaylistEntry dpe = playlist.remove(from);
			playlist.addAt(to, dpe);
			return true;
		}
	}
	public void onPlayEntry(String sessionId, String entryId) {
		onPlayEntry(sessions.get(sessionId), soundtracks.get(entryId));
	}
	public void onPlayEntry(Session session, TrackEntry entry) {
		
	}
	public void onPlaySession(Session session) {
		synchronized(dataLock) {
			getCurrentSessionData().set(session.id);
		}
	}

	public DataString getCurrentSessionData() {
		return sData.currentSession;
	}
	public GenericFileTree getFileTree() {
		return fTree;
	}

	private void createIDsForUnknownSoundtracks() throws IOException {
		List<GenericFileManager> files = fTree.getAll(Path.of("Soundtracks"));
		for(GenericFileManager f: files) {
			Path relativePath = fTree.getPath().relativize(f.getPath());
			String[] split = Util.getNameAndType(relativePath.toString());
			if(!split[1].equals(".txt")) {
				String id = UUID.randomUUID().toString();
				TrackEntry entry = new TrackEntry(relativePath, id);
				if(!soundtracks.containsValue(entry)) {
					readEntry(entry, fTree.getOrCreate(Path.of(split[0] + "_info.txt")));
					soundtracks.put(id, entry);
					stData.soundtracks.put(new DataString(id), new DataString(entry.path.toString()));
				}
			}
		}
		stDataSystem.save();
	}
	public void save() throws IOException {
		synchronized(dataLock) {
			soundtracks.forEach((id,entry) -> {
				stData.soundtracks.put(new DataString(id), new DataString(entry.path.toString()));
				try {
					if(entry.dataSystem != null)
						entry.dataSystem.save();
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			playlists.forEach((id,playlist) -> {
				try {
					if(playlist.dataSystem != null)
						playlist.dataSystem.save();
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			sessions.forEach((id,session) -> {
				try {
					if(session.dataSystem != null)
						session.dataSystem.save();
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			stDataSystem.save();
			plDataSystem.save();
			sDataSystem.save();
		}
	}

	public void correctPathData(Map<DataString, DataString> map) {
		//Default = doNothing
	}

	public List<TrackEntry> tracksSortedByName() {
		return soundtracks.values().stream()
				.sorted((a, b) -> naturalOrderComparator.compare(a,b))
				.toList();
	}
	public List<Playlist> playlistsSortedByName() {
		return playlists.values().stream()
				.sorted((a, b) -> naturalOrderComparator.compare(a,b))
				.toList();
	}
	public int playlistLength(Playlist playlist) {
		int ret = 0;
		for(DataPlaylistEntry dpe: playlist.getAll(playlists, false)) {
			TrackEntry entry = soundtracks.get(dpe.id.get());
			if(entry != null)
				ret += entry.length.get();
		}
		return ret;
	}
	public int playlistSize(Playlist playlist) {
		return playlist.getAll(playlists, false).size();
	}
}
