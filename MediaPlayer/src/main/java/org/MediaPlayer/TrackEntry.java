package org.MediaPlayer;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.MediaPlayer.DataTypes.DataPlaylistEntry;

import Tools.Files.Util;
import Tools.Files.Data.DataAdapter;
import Tools.Files.Data.DataType;
import Tools.Files.Data.DataTypes.*;

public class TrackEntry extends DataAdapter {
	DataString description;
	DataInt length;
	Path path;
	Set<String> inPlaylists;
	public String id;
	public TrackEntry(Path path, String id) {
		this.path = path;
		this.id = id;
		this.description = new DataString("");
		this.length = new DataInt();
		this.inPlaylists = new HashSet<>();
	}
	public String getName() {
		return Util.getNameAndType(Path.of("Soundtracks").relativize(path).toString())[0];
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof TrackEntry entry) {
			return entry.path.equals(this.path);
		}
		if(obj instanceof DataPlaylistEntry dpe) {
			return this.id.equals(dpe.id.get());
		}
		return super.equals(obj);
	}
	@Override
	public void createData(Map<String, DataType> data) {
		description = (DataString) data.get("Description");
		length = (DataInt) data.get("Length");
	}
}
