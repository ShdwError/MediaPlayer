package org.mediaplayer.core.connection.messages.actions;

import java.time.LocalDateTime;

import org.mediaplayer.core.connection.dtos.PlaylistDTO;
import org.mediaplayer.core.connection.messages.ActionKey;
import org.mediaplayer.core.connection.messages.GenericAction;

import Tools.Core.Files.Data.DataTypes.*;

public class PlaylistAction extends GenericAction {
	private PlaylistDTO playlist;
	public PlaylistAction() {
		this.date = new DataDate();
		this.playlist = new PlaylistDTO();
		
		createData(date, playlist);
	}
	public PlaylistAction(LocalDateTime dateTime, PlaylistDTO playlist) {
		this(new DataDate(dateTime), playlist);
	}
	public PlaylistAction(DataDate date, PlaylistDTO playlist) {
		this.date = date;
		this.playlist = playlist;
		
		createData(date, playlist);
		created = true;
	}
	public PlaylistDTO get() {
		return playlist;
	}
	@Override
	public ActionKey getKey() {
		return new ActionKey(playlist.getId());
	}
	

}
