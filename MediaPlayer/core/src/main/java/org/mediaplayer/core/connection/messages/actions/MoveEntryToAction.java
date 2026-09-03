package org.mediaplayer.core.connection.messages.actions;

import java.time.LocalDateTime;

import org.mediaplayer.core.connection.messages.ActionKey;
import org.mediaplayer.core.connection.messages.GenericAction;

import Tools.Core.Files.Data.DataTypes.DataDate;
import Tools.Core.Files.Data.DataTypes.DataInt;
import Tools.Core.Files.Data.DataTypes.DataString;

public class MoveEntryToAction extends GenericAction {
	private DataString playlistId;
	private DataString entryId;
	private DataInt toPos;
	public MoveEntryToAction() {
		this.date = new DataDate();
		this.playlistId = new DataString();
		this.entryId = new DataString();
		this.toPos = new DataInt();
		createData(date, playlistId, entryId, toPos);
	}
	public MoveEntryToAction(LocalDateTime dateTime, String playlistId, String entryId, int toPos) {
		this(new DataDate(dateTime), new DataString(playlistId), new DataString(entryId), new DataInt(toPos));
	}
	public MoveEntryToAction(DataDate date, DataString playlistId, DataString entryId, DataInt toPos) {
		this.date = date;
		this.playlistId = playlistId;
		this.entryId = entryId;
		this.toPos = toPos;
		createData(date, playlistId, entryId, toPos);
		created = true;
	}
	@Override
	public ActionKey getKey() {
		return new ActionKey(getDataType(), playlistId, entryId);
	}
	public String getPlaylistId() {
		return playlistId.get();
	}
	public String getEntryId() {
		return entryId.get();
	}
	public int getToPos() {
		return toPos.get();
	}
	

}
