package org.mediaplayer.server;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import org.mediaplayer.Tools.Connection.WebServer;
import org.mediaplayer.core.Session;
import org.mediaplayer.core.connection.ActionManager;
import org.mediaplayer.core.connection.ActionResult;
import org.mediaplayer.core.connection.messages.Action;
import org.mediaplayer.core.connection.messages.Change;
import org.mediaplayer.core.connection.messages.Message;
import org.mediaplayer.core.connection.messages.Request;
import org.mediaplayer.core.connection.messages.RequestType;
import org.mediaplayer.core.connection.messages.Sync;
import org.mediaplayer.core.connection.messages.requests.SyncFromRequest;

import Tools.Core.Files.DataSystem;
import Tools.Core.Files.FileManager;
import Tools.Core.Files.GenericFileManager;
import Tools.Core.Files.Data.Exceptions.DataTypeException;
import Tools.Core.Util.Threadsystem;

public class MediaPlayerServer extends WebServer {
	private ServerFileLogic fileLogic;
	
	private DataSystem<ServerData> dataSystem;
	private ServerData serverData;
	private ActionManager actionManager;
	
	public MediaPlayerServer(String host, int port, String name, Path path) throws IOException, DataTypeException {
		this(host, port, name, new FileManager(path), new Threadsystem(1));
	}
	public MediaPlayerServer(String host, int port, String name, GenericFileManager fileManager, Threadsystem threadsystem) throws IOException, DataTypeException {
		super(host, port, name, threadsystem);
		
		this.dataSystem = new DataSystem<>(fileManager, ServerData::new);
		dataSystem.read();
		serverData = dataSystem.getOrCreate();
	}
	public void create(ServerFileLogic fileLogic) throws Exception {
		this.fileLogic = fileLogic;
		this.actionManager = new ActionManager(fileLogic, true);
		this.run();
	}
	@Override
	public int getThreadAmount() {
		return 1;
	}
	
	@Override
	public void followInstruction(String request, ServerSideConnection connection) {
		Message message = new Message();
		try {
			message.setData(request);
			switch(message.getType()) {
			case "change": runChange((Change) message.get(), connection); break;
			case "sync": //TODO Error
			}
		}
		catch(DataTypeException e) {
			e.printStackTrace();
			//TODO Error
		}
	}
	public void runChange(Change change, ServerSideConnection connection) {
		Set<String> sessionsToSync = new HashSet<>();
		Change broadcastChange = new Change();
		
		for(Action actionData: change.getActions()) {
			if(!serverData.isValid(actionData)) {
				//Error --> Message?
				continue;
			}
			ActionResult result = actionManager.runAction(actionData, sessionsToSync);
			if(result == ActionResult.APPLIED) {
				serverData.addAction(actionData);
				broadcastChange.addAction(actionData);
				System.out.println("Applied");
			}
		}
		Sync sync = new Sync(serverData.currentVersion());
		for(String id: sessionsToSync) {
			Session session = fileLogic.sessions.get(id);
			if(session != null) {
				sync.addSession(session);
			}
		}
		if(!sync.isEmpty()) {
			Message message = new Message("sync", sync);
			connection.write(message.getData());
		}
		connection.instruct(new Message("sync", new Sync(serverData.currentVersion())).getData());
		
		broadcastChange.setVersion(serverData.currentVersion());
		broadcastInstruction(new Message("change", broadcastChange).getData(), connection);
	}
	@Override
	public String answerRequest(String request, ServerSideConnection connection) {
		Message message = new Message();
		try {
			message.setData(request);
			switch(message.getType())  {
			case "request": return answerRequest((Request) message.get());
			}
		}
		catch(DataTypeException e) {
			e.printStackTrace();
			//TODO Error
		}
		return null;
	}
	
	public String answerRequest(Request request) {
		RequestType requestType = request.get();
		switch(requestType.getType()) {
			case "syncFrom": {
				SyncFromRequest syncFrom = (SyncFromRequest) requestType.get();
				int version = syncFrom.getFromVersion();
				if(version <= 0)
					return new Message("sync", fullSync()).getData();
				
				Change change = serverData.syncFrom(version);
				return new Message("change", change).getData();
			}
		}
		return null;
	}
	public Sync fullSync() {
		Sync sync = new Sync(true);
		fileLogic.soundtracks.forEach((id, entry) -> {
			sync.addTrackEntry(entry);
		});
		fileLogic.playlists.forEach((id, playlist) -> {
			sync.addPlaylist(playlist);
		});
		fileLogic.sessions.forEach((id, session) -> {
			sync.addSession(session);
		});
		return sync;
	}
	
	public ServerData getServerData() {
		return serverData;
	}
}
