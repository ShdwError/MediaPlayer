package org.mediaplayer.core.connection.messages.actions;

import java.time.LocalDateTime;

import org.mediaplayer.core.DataPlaylistEntry;
import org.mediaplayer.core.connection.messages.ActionKey;
import org.mediaplayer.core.connection.messages.GenericAction;

import Tools.Core.Files.Data.DataTypes.DataDate;
import Tools.Core.Files.Data.DataTypes.DataString;

public class AddSoundtrackToAction extends GenericAction {
	private DataString playlistId;
	private DataPlaylistEntry dpe;
	public AddSoundtrackToAction() {
		this.date =  new DataDate();
		this.playlistId = new DataString();
		this.dpe = new DataPlaylistEntry();
		
		createData(date, playlistId, dpe);
	}
	public AddSoundtrackToAction(LocalDateTime dateTime, String playlistId, DataPlaylistEntry dpe) {
		this(new DataDate(dateTime), new DataString(playlistId), dpe);
	}
	public AddSoundtrackToAction(DataDate date, DataString playlistId, DataPlaylistEntry dpe) {
		this.date = date;
		this.playlistId = playlistId;
		this.dpe = dpe;
		
		createData(date, playlistId, dpe);
		created = true;
	}
	@Override
	public ActionKey getKey() {
		return new ActionKey(getDataType(), playlistId, dpe.id);
	}
	public String getPlaylistId() {
		return playlistId.get();
	}
	public DataPlaylistEntry getDpe() {
		return dpe;
	}
}
