package org.mediaplayer.core.connection.messages.actions;

import java.time.LocalDateTime;

import org.mediaplayer.core.connection.messages.ActionKey;
import org.mediaplayer.core.connection.messages.GenericAction;

import Tools.Core.Files.Data.DataTypes.DataDate;
import Tools.Core.Files.Data.DataTypes.DataString;

public class LinkEntryToAction extends GenericAction {
	private DataString playlistId;
	private DataString entryId;
	private DataString linkToId;
	public LinkEntryToAction() {
		this.date = new DataDate();
		this.playlistId = new DataString();
		this.entryId = new DataString();
		this.linkToId = new DataString();
		
		createData(date, playlistId, entryId, linkToId);
	}
	public LinkEntryToAction(LocalDateTime dateTime, String playlistId, DataString entryId, String linkToId) {
		this(new DataDate(dateTime), new DataString(playlistId), entryId, new DataString(linkToId));
	}
	public LinkEntryToAction(DataDate date, DataString playlistId, DataString entryId, DataString linkToId) {
		this.date = date;
		this.playlistId = playlistId;
		this.entryId = entryId;
		this.linkToId = linkToId;
		
		createData(date, playlistId, entryId, linkToId);
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
	public String getLinkToId() {
		return linkToId.get();
	}
	
}
