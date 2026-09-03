package org.mediaplayer.core.connection.dtos;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.mediaplayer.core.DataPlaylistEntry;
import org.mediaplayer.core.Session;

import Tools.Core.Files.Data.DataClass;
import Tools.Core.Files.Data.DataTypes.DataArray;
import Tools.Core.Files.Data.DataTypes.DataBoolean;
import Tools.Core.Files.Data.DataTypes.DataDate;
import Tools.Core.Files.Data.DataTypes.DataInt;
import Tools.Core.Files.Data.DataTypes.DataString;

public class SessionDTO extends DataClass {
	private DataString path;
	private DataString id;
	private List<DataPlaylistEntry> playlist;
	private DataInt pos;
	private DataBoolean loop;
	private DataDate createdOn;
	private DataDate lastOpend;
	public SessionDTO() {
		setup(new DataString(), new DataString(), new ArrayList<>(), new DataInt(), new DataBoolean(), new DataDate(), new DataDate());
	}
	public SessionDTO(Session session) {
		this(new DataString(session.path.toString()), new DataString(session.id), session.get(), session.pos, session.loop, session.created, session.lastOpened);
	}
	public SessionDTO(DataString path, DataString id, List<DataPlaylistEntry> playlist, DataInt pos, DataBoolean loop, DataDate createdOn, DataDate lastOpend) {
		setup(path, id, playlist, pos, loop, createdOn, lastOpend);
		created = true;
	}
	private void setup(DataString path, DataString id, List<DataPlaylistEntry> playlist, DataInt pos, DataBoolean loop, DataDate createdOn, DataDate lastOpend) {
		this.path = path;
		this.id = id;
		this.playlist = playlist;
		this.loop = loop;
		
		createData(path, id, new DataArray<DataPlaylistEntry>(DataPlaylistEntry::new, playlist), pos, loop, createdOn, lastOpend);
	}
	public Path getPath() {
		return Path.of(path.get());
	}
	public String getId() {
		return id.get();
	}
	public List<DataPlaylistEntry> getPlaylist() {
		return playlist;
	}
	public DataInt getPos() {
		return pos;
	}
	public DataBoolean shouldLoop() {
		return loop;
	}
	public DataDate getCreatedOn() {
		return createdOn;
	}
	public DataDate getLastOpend() {
		return lastOpend;
	}
}
