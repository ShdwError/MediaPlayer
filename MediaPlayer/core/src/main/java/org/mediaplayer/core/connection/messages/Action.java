package org.mediaplayer.core.connection.messages;

import org.mediaplayer.core.connection.messages.actions.*;

import Tools.Core.Files.Data.DataTypedClass;
import Tools.Core.Files.Data.DataTypes.DataString;

public class Action extends DataTypedClass<GenericAction> {
	
	public Action() {
		super();
	}
	public Action(String type, GenericAction action) {
		super(new DataString(type), action);
	}
	
	public void createTypes() {
		addType("createPlaylist", PlaylistAction::new);
		addType("createSession", SessionAction::new);
		addType("renameSoundtrack", RenameItemAction::new);
		addType("renamePlaylist", RenameItemAction::new);
		addType("renameSession", RenameItemAction::new);
		addType("deleteSoundtrack", DeleteItemAction::new);
		addType("deletePlaylist", DeleteItemAction::new);
		addType("deleteSession", DeleteItemAction::new);
		addType("addSoundtrackTo", AddSoundtrackToAction::new);
		addType("removeSoundtrackFrom", RemoveSoundtrackFromAction::new);
		addType("linkEntryTo", LinkEntryToAction::new);
		addType("addSubplaylist", ModifySubplaylistAction::new);
		addType("removeSubplaylist", ModifySubplaylistAction::new);
		addType("setSession", SessionAction::new);
		addType("moveEntryTo", MoveEntryToAction::new);
		addType("playEntry", PlayEntryAction::new);
	}

}
