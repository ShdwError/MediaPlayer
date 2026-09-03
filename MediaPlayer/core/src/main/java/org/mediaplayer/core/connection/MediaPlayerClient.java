package org.mediaplayer.core.connection;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.mediaplayer.core.Playlist;
import org.mediaplayer.core.Session;
import org.mediaplayer.core.TrackEntry;
import org.mediaplayer.core.Generics.GenericAudio;
import org.mediaplayer.core.Generics.GenericFileLogic;
import org.mediaplayer.core.Generics.GenericUI;
import org.mediaplayer.core.Interfaces.ClientConnectionManager;
import org.mediaplayer.core.connection.dtos.PlaylistDTO;
import org.mediaplayer.core.connection.dtos.SessionDTO;
import org.mediaplayer.core.connection.dtos.TrackEntryDTO;
import org.mediaplayer.core.connection.messages.Action;
import org.mediaplayer.core.connection.messages.Change;
import org.mediaplayer.core.connection.messages.GenericAction;
import org.mediaplayer.core.connection.messages.Message;
import org.mediaplayer.core.connection.messages.Request;
import org.mediaplayer.core.connection.messages.RequestType;
import org.mediaplayer.core.connection.messages.Sync;
import org.mediaplayer.core.connection.messages.actions.PlaylistAction;
import org.mediaplayer.core.connection.messages.actions.SessionAction;
import org.mediaplayer.core.connection.messages.requests.SyncFromRequest;

import Tools.Core.Connection.Connactable;
import Tools.Core.Files.DataSystem;
import Tools.Core.Files.FileManager;
import Tools.Core.Files.GenericFileManager;
import Tools.Core.Files.Data.DataTypes.DataInt;
import Tools.Core.Files.Data.Exceptions.DataTypeException;

public class MediaPlayerClient implements ClientConnectionManager {
	protected GenericFileLogic fileLogic;
	protected GenericAudio audio;
	protected GenericUI ui;
	
	private DataSystem<ClientData> dataSystem;
	private ClientData clientData;
	
	private ActionManager actionManager;
	private Connactable client;
	
	public MediaPlayerClient(Path path) throws IOException, DataTypeException {
		this(new FileManager(path));
	}
	public MediaPlayerClient(GenericFileManager fileManager) throws IOException, DataTypeException {
		this.dataSystem = new DataSystem<>(fileManager, ClientData::new);
		dataSystem.read();
		clientData = dataSystem.getOrCreate();
	}
	public void create(GenericFileLogic fileLogic, GenericAudio audio, GenericUI ui, Connactable client) {
		this.fileLogic = fileLogic;
		this.audio = audio;
		this.ui = ui;
		this.actionManager = new ActionManager(fileLogic, false);
		this.client = client;
	}
	@Override
	public void followInstruction(String request) {
		Message message = new Message();
		try {
			message.setData(request);
			switch(message.getType()) {
			case "change": runChange((Change) message.get()); break;
			case "sync": applySync((Sync) message.get()); break;
			}
		}
		catch(DataTypeException e) {
			e.printStackTrace();
			//TODO Error
		}
	}
	public void runChange(Change change) {
		//TODO Events
		for(Action actionData: change.getActions()) {
			ActionResult result = actionManager.runAction(actionData, Set.of());
			//TODO UI Events
			if(result == ActionResult.REJECTED) {
				System.out.println("Could not apply action " + actionData);
				//TODO Error
			}
		}
		System.out.println("Set Version: " + change.getVersion());
		clientData.version.set(change.getVersion());
	}
	public void applySync(Sync sync) {
		//TODO Events
		if(sync.isFullSync()) {
			fileLogic.soundtracks.clear();
			fileLogic.playlists.clear();
			fileLogic.sessions.clear();
		}
		sync.getSoundtracks().forEach((id, entryDTO) -> {
			TrackEntry entry = toEntry(id.get(), entryDTO);
			fileLogic.soundtracks.put(id.get(), entry);
		});
		sync.getPlaylists().forEach((id, playlistDTO) -> {
			Playlist playlist = toPlaylist(id.get(), playlistDTO);
			for(int i = 0; i < playlist.size(); i++) {
				TrackEntry entry = fileLogic.soundtracks.get(playlist.getId(i));
				if(entry != null) entry.inPlaylists.add(id.get());
			}
			fileLogic.playlists.put(id.get(), playlist);
		});
		sync.getSessions().forEach((id, sessionDTO) -> {
			Session session = toSession(id.get(), sessionDTO);
			fileLogic.sessions.put(id.get(), session);
		});
		System.out.println("Set Version: " + sync.getVersion());
		clientData.version.set(sync.getVersion());
		audio.reset();
		ui.resetAll();
	}
	@Override
	public String answerRequest(String request) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void connectionCreated() {
		// TODO Auto-generated method stub
	}
	@Override
	public void onDisconnect(int statusCode, String reason) {
		if(client != null)
			client.connect();
	}
	private TrackEntry toEntry(String id, TrackEntryDTO entryDTO) {
		return new TrackEntry(entryDTO.getPath(), entryDTO.getDescription(), entryDTO.getLength(), id);
	}
	private Playlist toPlaylist(String id, PlaylistDTO playlistDTO) {
		return new Playlist(playlistDTO.getPath(), playlistDTO.getPlaylist(), playlistDTO.getSubplaylists(), id);
	}
	private Session toSession(String id, SessionDTO sessionDTO) {
		return new Session(sessionDTO.getPath(), id, sessionDTO.getPlaylist(), sessionDTO.getPos(), sessionDTO.shouldLoop(), sessionDTO.getCreatedOn(), sessionDTO.getLastOpend());
	}
	
	public void requestSync() throws ExecutionException, InterruptedException {
		SyncFromRequest syncRequest = new SyncFromRequest(clientData.version);
		Request request = new Request(new RequestType("syncFrom", syncRequest));
		Message message = new Message("request", request);
		String answer = client.request(message.getData());
		followInstruction(answer);
	}
	
	public Playlist createPlaylist(Path path, List<String> ids, List<String> subIds) throws IOException {
		Playlist playlist = fileLogic.createPlaylist(path, ids, subIds);
		if(playlist == null)
			return null;
		
		PlaylistDTO playlistDTO = new PlaylistDTO(playlist);
		PlaylistAction action = new PlaylistAction(LocalDateTime.now(), playlistDTO);
		writeAction("createPlaylist", action);
		return playlist;
	}
	public Session createSession(List<Playlist> sessionParts, List<String> entryIds, Path path, boolean looping, boolean shuffle) throws IOException {
		Session session = fileLogic.createSession(sessionParts, entryIds, path, looping, shuffle);
		if(session == null)
			return null;
		
		SessionDTO sessionDTO = new SessionDTO(session);
		SessionAction action = new SessionAction(LocalDateTime.now(), sessionDTO);
		writeAction("createSession", action);
		return session;
	}
	
	private void writeAction(String actionType, GenericAction action) {
		Change change = new Change(clientData.version, new Action(actionType, action));
		Message message = new Message("change", change);
		client.instruct(message.getData());
	}
	
	public ClientData getClientData() {
		return clientData;
	}
	
}
