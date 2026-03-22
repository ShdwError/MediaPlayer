package org.mediaplayer.desktop;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import org.mediaplayer.core.App;
import org.mediaplayer.core.Playlist;
import org.mediaplayer.core.Session;
import org.mediaplayer.core.TrackEntry;
import org.mediaplayer.core.UtilFunctions;
import org.mediaplayer.core.Data.PlaylistsData;
import org.mediaplayer.core.Data.SessionsData;
import org.mediaplayer.core.Data.SoundtracksData;
import org.mediaplayer.core.DataTypes.DataPlaylistEntry;
import org.mediaplayer.core.Generics.GenericFileLogic;

import Tools.Files.DataSystem;
import Tools.Files.FileManager;
import Tools.Files.FileTree;
import Tools.Files.Util;
import Tools.Files.Data.DataContainer;
import Tools.Files.Data.DataTypes.DataArray;
import Tools.Files.Data.DataTypes.DataBoolean;
import Tools.Files.Data.DataTypes.DataDate;
import Tools.Files.Data.DataTypes.DataInt;
import Tools.Files.Data.DataTypes.DataMap;
import Tools.Files.Data.DataTypes.DataString;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class DesktopFileLogic extends GenericFileLogic {
	
	private FileTree fTree;
	private Path path;
	private ConsoleUI ui;
	private DesktopAudio audio;
	
	private DataSystem stDataSystem;
	private SoundtracksData stData;
	private DataSystem plDataSystem;
	private PlaylistsData plData;
	private DataSystem sDataSystem;
	private SessionsData sData;
	
	public DesktopFileLogic(Path path) throws IOException {
		super();
		
		this.path = path;
		
		fTree = new FileTree(path);
		fTree.createFile(Path.of("Soundtracks.txt"));
		fTree.createFile(Path.of("Playlists.txt"));
		fTree.createFile(Path.of("Sessions.txt"));
		fTree.createFolder(Path.of("Soundtracks"));
		fTree.createFolder(Path.of("Playlists"));
		fTree.createFolder(Path.of("Sessions"));
	}
	
	@Override
	public void create(App app) throws IOException {
		this.ui = (ConsoleUI) app.ui;
		this.audio = (DesktopAudio) app.audio;
		
		loadSoundtracks();
		loadPlaylists();
		loadSessions();
		
		createIDsForUnknownSoundtracks();
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
		playlist.reorganize();
		playlist.dataContainer.system.save();
		plData.dataContainer.system.save();
	}
	public Session createSession(List<Playlist> sessionParts, Path path, boolean looping, boolean shuffle) throws IOException {
		String id = UUID.randomUUID().toString();
		sData.sessions.put(id, new DataString(path.toString()));
		FileManager fm = fTree.createFile(path);
		Session session = readSession(fm, path, id);
		
		for(Playlist playlist: sessionParts) {
			session.add(playlist.getAll(playlists, true));
		}
		session.loop.set(looping);
		if(shuffle) session.shuffle(false);
		
		session.reorganize();
		
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
			FileManager infoFM = fTree.getOrCreate(UtilFunctions.getSoundtrackInfoPath(cPath));
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
			//if(playlist == null) plData.playlists.remove(id);
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
			if(session == null) sData.sessions.remove(id);
		});
	}
	//Read Functions
	public TrackEntry readEntry(TrackEntry entry, FileManager fm) {
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
					 player.dispose();
				 });
			}
		}
		return entry;
	}
	public Playlist readPlaylist(FileManager fm, Path path, String id) {
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
				playlist.reorganize();
				playlists.put(id, playlist);
			}
			return playlist;
		}
		return null;
	}
	public Session readSession(FileManager fm, Path path, String id)  {
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
			if(session != null) {
				session.reorganize();
				sessions.put(id, session);
			}
			return session;
		}
		return null;
	}
	//Change Functions
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
			if(entry.id.equals(audio.getCurrentTrack().id) && audio.hasPlayer()) {
				audio.closePlayer();
				System.gc();
			}
			newPath = fTree.getNextFreeFileName(newPath);
			Path oldPath = entry.path;
	
			entry.path = newPath;
			stData.soundtracks.put(entry.id, new DataString(newPath.toString()));
			
			fTree.rename(oldPath, newPath);
			FileManager newTrackManager = fTree.rename(UtilFunctions.getSoundtrackInfoPath(oldPath), UtilFunctions.getSoundtrackInfoPath(newPath));
			
			entry.dataContainer.system.changeFileManager(newTrackManager);
			stDataSystem.save();
			
			if(entry.id.equals(audio.getCurrentTrack().id)) {
				audio.playAndSetNext(entry);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void deleteSoundtrack(TrackEntry entry) {
		try {
			for(String id: entry.inPlaylists) {
				playlists.get(id).remove(new DataPlaylistEntry(new DataString(entry.id), null));
			}
			if(entry.id.equals(audio.getCurrentTrack().id) && audio.hasPlayer()) {
				audio.playNextTrack();
				System.gc();
			}
			fTree.remove(entry.path);
			fTree.remove(UtilFunctions.getSoundtrackInfoPath(entry.path));
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
			if(audio.getCurrentSession() == session) {
				audio.stopSession();
			}
			fTree.remove(session.path);
			sData.sessions.remove(session.id);
			sessions.remove(session.id);
			sDataSystem.save();
		} catch (IOException e) {
				e.printStackTrace();
		}
	}
	
	@Override
	public DataString getCurrentSessionData() {
		return sData.currentSession;
	}
	public FileTree getFileTree() {
		return fTree;
	}
	
	public void createIDsForUnknownSoundtracks() throws IOException {
		List<FileManager> files = fTree.getAll(Path.of("Soundtracks"));
		for(FileManager f: files) {
			String[] split = Util.getNameAndType(f.getPath().toString());
			if(!split[1].equals(".txt")) {
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
}
