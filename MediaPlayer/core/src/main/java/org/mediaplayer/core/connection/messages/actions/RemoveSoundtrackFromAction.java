package org.mediaplayer.core.connection.messages.actions;

import java.time.LocalDateTime;

import org.mediaplayer.core.connection.messages.ActionKey;
import org.mediaplayer.core.connection.messages.GenericAction;

import Tools.Core.Files.Data.DataTypes.DataDate;
import Tools.Core.Files.Data.DataTypes.DataString;

public class RemoveSoundtrackFromAction extends GenericAction {
	private DataString entryId;
	private DataString playlistId;
	public RemoveSoundtrackFromAction()  {
		this.date = new DataDate();
		this.entryId = new DataString();
		this.playlistId = new DataString();
		
		createData(date, entryId, playlistId);
	}
	public RemoveSoundtrackFromAction(LocalDateTime dateTime, String entryId, String playlistId) {
		this(new DataDate(dateTime), new DataString(entryId), new DataString(playlistId));
	}
	public RemoveSoundtrackFromAction(DataDate date, DataString entryId, DataString playlistId) {
		this.date = date;
		this.entryId = entryId;
		this.playlistId = playlistId;
		
		createData(date, entryId, playlistId);
		created = true;
	}
	@Override
	public ActionKey getKey() {
		return new ActionKey(getDataType(), entryId, playlistId);
	}
	public String getEntryId() {
		return entryId.get();
	}
	public String getPlaylistId() {
		return playlistId.get();
	}

}
