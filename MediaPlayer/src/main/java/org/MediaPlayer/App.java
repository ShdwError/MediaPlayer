package org.MediaPlayer;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.MediaPlayer.Data.PlaylistsData;
import org.MediaPlayer.Data.SessionsData;
import org.MediaPlayer.Data.SoundtracksData;
import org.MediaPlayer.DataTypes.DataPlaylistEntry;

import Tools.Files.DataSystem;
import Tools.Files.FileManager;
import Tools.Files.FileTree;
import Tools.Files.Util;
import Tools.Files.Data.DataContainer;
import Tools.Files.Data.DataType;
import Tools.Files.Data.DataTypes.*;
import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class App {
	Map<String, TrackEntry> soundtracks;
	SoundtracksData stData;
	DataSystem stDataSystem;
	Map<String, Playlist> playlists;
	PlaylistsData plData;
	DataSystem plDataSystem;
	Map<String, Session> sessions;
	SessionsData sData;
	DataSystem sDataSystem;
	
	Session currentSession;
	TrackEntry currentTrack;
	
	FileTree fTree;
	private Path path;
	
	public static MediaPlayer mediaPlayer;
	
	AppUI ui;
	
	public App(Path path) throws IOException {
		this.path = path;
		
		soundtracks = new HashMap<>();
		playlists = new HashMap<>();
		sessions = new HashMap<>();
		
		fTree = new FileTree(path);
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
		
		startUI();
	}
	public void startUI() {
		this.ui = new AppUI(this);
		ui.startUI();
	}
	//Helper functions
	public void renameSession(Session session, Path newPath) {
		newPath = fTree.getNextFreeFileName(newPath);
		Path oldPath = session.path;

		session.path = newPath;
		sData.sessions.put(session.id, new DataString(newPath.toString()));
		try {
			session.dataContainer.system.changeFileManager(fTree.rename(oldPath, newPath));
			sDataSystem.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void renamePlaylist(Playlist playlist, Path newPath) {
		newPath = fTree.getNextFreeFileName(newPath);
		Path oldPath = playlist.path;

		playlist.path = newPath;
		plData.playlists.put(playlist.id, new DataString(newPath.toString()));
		try {
			playlist.dataContainer.system.changeFileManager(fTree.rename(oldPath, newPath));
			plDataSystem.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void renameSoundtrack(TrackEntry entry, Path newPath) {
		try {
			newPath = fTree.getNextFreeFileName(newPath);
			Path oldPath = entry.path;
	
			entry.path = newPath;
			stData.soundtracks.put(entry.id, new DataString(newPath.toString()));
			
			FileManager newTrackManager = fTree.rename(oldPath, newPath);
			fTree.rename(getSoundtrackInfoPath(oldPath), getSoundtrackInfoPath(newPath));
			
			entry.dataContainer.system.changeFileManager(newTrackManager);
			stDataSystem.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void deleteSoundtrack(TrackEntry entry) {
		try {
			fTree.remove(entry.path);
			fTree.remove(getSoundtrackInfoPath(entry.path));
			stData.soundtracks.remove(entry.id);
			soundtracks.remove(entry.id);
			stDataSystem.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void deletePlaylist(Playlist playlist) {
		try {
			fTree.remove(playlist.path);
			plData.playlists.remove(playlist.id);
			playlists.remove(playlist.id);
			plDataSystem.save();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void deleteSession(Session session) {
		try {
			if(session == currentSession) {
				stopSession();
			}
			fTree.remove(session.path);
			sData.sessions.remove(session.id);
			sessions.remove(session.id);
			sDataSystem.save();
		} catch (IOException e) {
				e.printStackTrace();
		}
	}
	public void removeSoundtrack(Session session, TrackEntry entry) {
		session.remove(entry);
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
		sData.currentSession.set(session.id);
		DataPlaylistEntry dpe = session.getCurrent();
		if(dpe == null) {
			return;
		}
		TrackEntry entry = soundtracks.get(dpe.id.get());
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
			deleteSession(currentSession);
			System.out.println("End of Playlist");
			return;
		}
		
		System.out.println("Play next Track " + currentSession.pos.get());
		
		ui.printPlaylistPreview();
		
		TrackEntry entry = soundtracks.get(dpe.id.get());
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
		
		TrackEntry entry = soundtracks.get(dpe.id.get());
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
		
		TrackEntry entry = soundtracks.get(dpe.id.get());
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
	public void stopSession() {
		if(mediaPlayer == null) return;
		currentSession = null;
		sData.currentSession.created = false;
		
		mediaPlayer.setOnEndOfMedia(null);
		mediaPlayer.stop();
		mediaPlayer.dispose();
		mediaPlayer = null;
	}
	//Create Functions
	public void createPlaylist(Path path, List<String> ids, List<String> subIds) throws IOException {
		String id = UUID.randomUUID().toString();
		plData.playlists.put(id, new DataString(path.toString()));
		FileManager fm = fTree.createFile(path);
		Playlist playlist = readPlaylist(fm, path, id);
		for(String entryId: ids) {
			TrackEntry tEntry = soundtracks.get(entryId);
			if(tEntry != null) {
				tEntry.inPlaylists.add(entryId);
				DataPlaylistEntry entry = new DataPlaylistEntry(new DataString(entryId), new DataMap<DataString>(DataString::new));
				playlist.add(entry);
			}
		}
		for(String subPlaylistId: subIds) {
			playlist.addSubplaylist(subPlaylistId);
		}
		playlist.dataContainer.system.save();
		plData.dataContainer.system.save();
	}
	public Session createSession(List<Playlist> sessionParts, Path path, boolean looping, boolean shuffle) throws IOException {
		String id = UUID.randomUUID().toString();
		sData.sessions.put(id, new DataString(path.toString()));
		FileManager fm = fTree.createFile(path);
		Session session = readSession(fm, path, id);
		
		for(Playlist playlist: sessionParts) {
			session.add(playlist.getAll(playlists, new HashSet<String>(), true));
		}
		session.loop.set(looping);
		if(shuffle) session.shuffle(false);
		
		session.dataContainer.system.save();
		sData.dataContainer.system.save();
		
		return session;
	}
	//Load Functions
	public void loadSoundtracks() throws IOException  {
		stDataSystem = new DataSystem(fTree.get(Path.of("Soundtracks.txt")));
		stDataSystem.setAdapter(SoundtracksData::new);
		stDataSystem.addContainerData("Soundtracks", new DataMap<DataString>(DataString::new));
		stDataSystem.read();
		DataContainer dc = stDataSystem.getOrCreate("");
		
		stData = (SoundtracksData) dc.adapter;
		stData.soundtracks.forEach((id, data) -> {
			Path cPath = Path.of(data.get());
			FileManager infoFM = fTree.getOrCreate(getSoundtrackInfoPath(cPath));
			TrackEntry entry = readEntry(new TrackEntry(cPath, id), infoFM);
			soundtracks.put(id, entry);
		});
	}
	public void loadPlaylists() throws IOException {
		plDataSystem = new DataSystem(fTree.get(Path.of("Playlists.txt")));
		plDataSystem.setAdapter(PlaylistsData::new);
		plDataSystem.addContainerData("Playlists", new DataMap<DataString>(DataString::new));
		plDataSystem.read();
		DataContainer dc = plDataSystem.getOrCreate("");
		
		plData = (PlaylistsData) dc.adapter;
		plData.playlists.forEach((id, data) -> {
			FileManager fm = fTree.get(Path.of(data.get()));
			Playlist playlist = readPlaylist(fm, Path.of(data.get()), id);
		});
	}
	public void loadSessions() throws IOException {
		sDataSystem = new DataSystem(fTree.get(Path.of("Sessions.txt")));
		sDataSystem.setAdapter(SessionsData::new);
		sDataSystem.addContainerData("Sessions", new DataMap<DataString>(DataString::new));
		sDataSystem.addContainerData("Current", new DataString());
		sDataSystem.read();
		DataContainer dc = sDataSystem.getOrCreate("");
		
		sData = (SessionsData) dc.adapter;
		sData.sessions.forEach((id, data) -> {
			FileManager fm = fTree.get(Path.of(data.get()));
			Session session = readSession(fm, Path.of(data.get()), id);
		});
	}
	//Read Functions
	private TrackEntry readEntry(TrackEntry entry, FileManager fm) {
		if(fm != null) {
			DataSystem ds = new DataSystem(fm);
			ds.setAdapter(() -> {
				return entry;
				});
			ds.addContainerData("Description", new DataString());
			ds.addContainerData("Length", new DataInt());
			try {
				ds.read();
				ds.createNewDataContainer("");
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(!entry.length.created) {
				Media media = new Media(path.resolve(entry.path).toUri().toString());
				MediaPlayer player = new MediaPlayer(media);
				 player.setOnReady(() -> {
					 int sec = (int) media.getDuration().toSeconds();
					 entry.length.set(sec);
				 });
			}
		}
		return entry;
	}
	private Playlist readPlaylist(FileManager fm, Path path, String id) {
		if(fm != null) {
			DataSystem ds = new DataSystem(fm);
			ds.setAdapter(() -> {
				return new Playlist(path, id);
				});
			ds.addContainerData("Description", new DataString());
			ds.addContainerData("Playlist", new DataArray<DataPlaylistEntry>(DataPlaylistEntry::new));
			ds.addContainerData("Subplaylists", new DataArray<DataString>(DataString::new));
			try {
				ds.read();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Playlist playlist =  (Playlist) ds.getOrCreate("").adapter;
			if(playlist != null) {
				for(int i = 0; i < playlist.size(); i++) {
					TrackEntry entry = soundtracks.get(playlist.getId(i));
					if(entry != null) entry.inPlaylists.add(id);
				}
				playlists.put(id, playlist);
			}
			return playlist;
		}
		return null;
	}
	private Session readSession(FileManager fm, Path path, String id)  {
		if(fm != null) {
			if(sessions.containsKey(id)) return sessions.get(id);
			DataSystem ds = new DataSystem(fm);
			ds.setAdapter(() -> {
				return new Session(path, id);
				});
			ds.addContainerData("Description", new DataString());
			ds.addContainerData("Playlist", new DataArray<DataPlaylistEntry>(DataPlaylistEntry::new));
			ds.addContainerData("Position", new DataInt());
			ds.addContainerData("Loop", new DataBoolean()); 
			ds.addContainerData("CreatedOn", new DataDate(true)); 
			ds.addContainerData("LastOpened", new DataDate()); 
			
			try {
				ds.read();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Session session =  (Session) ds.getOrCreate("").adapter;
			if(session != null)
				sessions.put(id, session);
			return session;
		}
		return null;
	}
	
	public void createIDsForUnknownSoundtracks() throws IOException {
		List<FileManager> files = fTree.getAll(Path.of("Soundtracks"));
		for(FileManager f: files) {
			String[] split = Util.getNameAndType(f.getPath().toString());
			if(split[1].equals(".mp3")) {
				String id = UUID.randomUUID().toString();
				TrackEntry entry = new TrackEntry(path.relativize(f.getPath()), id);
				if(!soundtracks.containsValue(entry)) {
					readEntry(entry, fTree.getOrCreate(Path.of(split[0] + "_info.txt")));
					soundtracks.put(id, entry);
				}
			}
		}
	}
	public void save() throws IOException {
		soundtracks.forEach((id,entry) -> {
			stData.soundtracks.put(id, new DataString(entry.path.toString()));
			try {
				if(entry.dataContainer != null) 
					entry.dataContainer.system.save();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		playlists.forEach((id,playlist) -> {
			try {
				if(playlist.dataContainer != null)
					playlist.dataContainer.system.save();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		sessions.forEach((id,session) -> {
			try {
				if(session.dataContainer != null)
					session.dataContainer.system.save();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		stDataSystem.save();
		plDataSystem.save();
		sDataSystem.save();
	}
	

	private Path getSoundtrackInfoPath(Path path) {
		return Path.of(Util.getNameAndType(path.toString())[0] + "_info.txt");
	}
}
