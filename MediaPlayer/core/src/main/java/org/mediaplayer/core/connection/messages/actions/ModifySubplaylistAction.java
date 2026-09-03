package org.mediaplayer.core.connection.messages.actions;

import java.time.LocalDateTime;

import org.mediaplayer.core.connection.messages.ActionKey;
import org.mediaplayer.core.connection.messages.GenericAction;

import Tools.Core.Files.Data.DataTypes.DataDate;
import Tools.Core.Files.Data.DataTypes.DataString;

public class ModifySubplaylistAction extends GenericAction {
	private DataString playlistId;
	private DataString subplaylistId;
	public ModifySubplaylistAction() {
		this.date = new DataDate();
		this.playlistId = new DataString();
		this.subplaylistId = new DataString();
		createData(date, playlistId, subplaylistId);
	}
	public ModifySubplaylistAction(LocalDateTime dateTime, String playlistId, String subplaylistId) {
		this(new DataDate(dateTime), new DataString(playlistId), new DataString(subplaylistId));
	}
	public ModifySubplaylistAction(DataDate date, DataString playlistId, DataString subplaylistId) {
		this.date = date;
		this.playlistId = playlistId;
		this.subplaylistId = subplaylistId;
		createData(date, playlistId, subplaylistId);
		created = true;
	}
	@Override
	public ActionKey getKey() {
		return null;
	}
	public String getPlaylistId() {
		return playlistId.get();
	}
	public String getSubplaylistId() {
		return subplaylistId.get();
	}
	
}
