package org.mediaplayer.core.connection;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mediaplayer.core.DataPlaylistEntry;
import org.mediaplayer.core.Playlist;
import org.mediaplayer.core.Session;
import org.mediaplayer.core.Generics.GenericFileLogic;
import org.mediaplayer.core.connection.dtos.PlaylistDTO;
import org.mediaplayer.core.connection.dtos.SessionDTO;
import org.mediaplayer.core.connection.messages.Action;
import org.mediaplayer.core.connection.messages.actions.*;

public class ActionManager {
	private GenericFileLogic fileLogic;
	private boolean isAutorative;
	public ActionManager(GenericFileLogic fileLogic, boolean isAutorative) {
		this.fileLogic = fileLogic;
		this.isAutorative = isAutorative;
	}
	public ActionResult runAction(Action actionData, Set<String> sessionsToSync) {
		try {
			System.out.println(actionData.getType());
			switch(actionData.getType()) {
				case "createPlaylist": {
					PlaylistAction action = (PlaylistAction) actionData.get();
					PlaylistDTO playlist = action.get();
					Playlist created = fileLogic.createPlaylist(playlist.getPath(), playlist.getId(), playlist.getPlaylist(), playlist.getSubplaylists());
					System.out.println("created: " + created.get());
					return actionApplied(created != null);
				}
				case "createSession": {
					SessionAction action = (SessionAction) actionData.get();
					SessionDTO session = action.get();
					Session created = fileLogic.createSession(session.getPath(), session.getId(), session.getPlaylist(), session.shouldLoop().get());
					return actionApplied(created != null);
				}
				case "renameSoundtrack": {
					RenameItemAction action = (RenameItemAction) actionData.get();
					return actionApplied(fileLogic.renameSoundtrack(action.getId(), action.getPath()));
				}
				case "renamePlaylist": {
					RenameItemAction action = (RenameItemAction) actionData.get();
					return actionApplied(fileLogic.renamePlaylist(action.getId(), action.getPath()));
				}
				case "renameSession": {
					RenameItemAction action = (RenameItemAction) actionData.get();
					return actionApplied(fileLogic.renameSession(action.getId(), action.getPath()));
				}
				case "deleteSoundtrack": {
					DeleteItemAction action = (DeleteItemAction) actionData.get();
					return actionApplied(fileLogic.deleteSoundtrack(action.getId()));
				}
				case "deletePlaylist": {
					DeleteItemAction action = (DeleteItemAction) actionData.get();
					return actionApplied(fileLogic.deletePlaylist(action.getId()));
				}
				case "deleteSession": {
					DeleteItemAction action = (DeleteItemAction) actionData.get();
					return actionApplied(fileLogic.deleteSession(action.getId()));
				}
				case "addSoundtrackTo": {
					AddSoundtrackToAction action = (AddSoundtrackToAction) actionData.get();
					return actionApplied(fileLogic.addSoundtrackTo(action.getDpe(), action.getPlaylistId()));
				}
				case "removeSoundtrackFrom": {
					RemoveSoundtrackFromAction action = (RemoveSoundtrackFromAction) actionData.get();
					return actionApplied(fileLogic.removeSoundtrackFrom(action.getEntryId(), action.getPlaylistId()));
				}
				case "linkEntryTo": {
					LinkEntryToAction action = (LinkEntryToAction) actionData.get();
					return actionApplied(fileLogic.linkEntryTo(action.getPlaylistId(), action.getEntryId(), action.getLinkToId()));
				}
				case "addSubplaylist": {
					ModifySubplaylistAction action = (ModifySubplaylistAction) actionData.get();
					return actionApplied(fileLogic.addSubplaylist(action.getPlaylistId(), action.getSubplaylistId()));
				}
				case "removeSubplaylist": {
					ModifySubplaylistAction action = (ModifySubplaylistAction) actionData.get();
					return actionApplied(fileLogic.removeSubplaylist(action.getPlaylistId(), action.getSubplaylistId()));
				}
				case "setSession": {
					SessionAction action = (SessionAction) actionData.get();
					SessionDTO sessionDTO = action.get();
					Session session = fileLogic.sessions.get(sessionDTO.getId());
					if(session == null) return ActionResult.REJECTED;
					if(isAutorative && !isSamePlaylist(session.get(), sessionDTO.getPlaylist())) {
						sessionsToSync.add(sessionDTO.getId()); 
						return ActionResult.REJECTED;
					}
					session.set(sessionDTO.getPlaylist());
					return ActionResult.APPLIED;
				}
				case "moveEntryTo": {
					MoveEntryToAction action = (MoveEntryToAction) actionData.get();
					return actionApplied(fileLogic.moveEntryTo(action.getPlaylistId(), action.getEntryId(), action.getToPos()));
				}
				case "playEntry": {
					PlayEntryAction action = (PlayEntryAction) actionData.get();
					Session session = fileLogic.sessions.get(action.getSessionId());
					if(session == null) return ActionResult.REJECTED;
					session.moveTo(action.getEntryId());
					return ActionResult.APPLIED_NOT_PERSISTED;
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
			//TODO Error
		}
		return ActionResult.REJECTED;
	}
	private boolean isSamePlaylist(List<DataPlaylistEntry> playlist, List<DataPlaylistEntry> other) {
		if(playlist.size() != other.size())
			return false;
		Map<String, DataPlaylistEntry> idMap = new HashMap<>();
		for(DataPlaylistEntry entry: playlist) {
			idMap.put(entry.id.get(), entry);
		}
		for(DataPlaylistEntry entry: other) {
			DataPlaylistEntry compare = idMap.get(entry.id.get());
			if(compare == null || !compare.tags.equals(entry.tags))
				return false;
		}
		return true;
	}
	private ActionResult actionApplied(boolean applied) {
		if(applied) return ActionResult.APPLIED;
		return ActionResult.REJECTED;
	}
}
