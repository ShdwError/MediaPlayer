package org.mediaplayer.core;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.mediaplayer.core.DataTypes.DataPlaylistEntry;

import Tools.Files.Util;
import Tools.Files.Data.DataAdapter;
import Tools.Files.Data.DataType;
import Tools.Files.Data.DataTypes.*;

public class TrackEntry extends DataAdapter {
	public DataString description;
	public DataInt length;
	public Path path;
	public String id;
	public Set<String> inPlaylists;
	public TrackEntry(Path path, String id) {
		this.description = new DataString();
		this.length = new DataInt();
		this.path = path;
		this.id = id;
		this.inPlaylists = new HashSet<>();
	}
	public String getName() {
		return Util.getNameAndType(Path.of("Soundtracks").relativize(path).toString())[0];
	}
	@Override
	public String toString() {
		return getName();
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
	public void createMapping(Map<String, DataType> data) {
		data.put("Description", description);
		data.put("Length", length);
	}
}
