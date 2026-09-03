package org.mediaplayer.core;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import Tools.Core.Files.Util;
import Tools.Core.Files.Data.DataAdapter;
import Tools.Core.Files.Data.DataType;
import Tools.Core.Files.Data.DataTypes.*;

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
	public TrackEntry(Path path, DataString description, DataInt length, String id) {
		this.description = description;
		this.length = length;
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
