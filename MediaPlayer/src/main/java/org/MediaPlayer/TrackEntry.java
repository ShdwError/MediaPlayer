package org.MediaPlayer;

import java.nio.file.Path;
import java.util.Map;

import Tools.Files.Data.DataAdapter;
import Tools.Files.Data.DataType;
import Tools.Files.Data.Util;
import Tools.Files.Data.DataTypes.*;

public class TrackEntry extends DataAdapter {
	DataString description;
	DataInt length;
	Path path;
	public String id;
	public TrackEntry(Path path, String id) {
		this.path = path;
		this.id = id;
		this.description = new DataString("");
		this.length = new DataInt();
	}
	public String getName() {
		return Util.getNameAndType(Path.of("Soundtracks").relativize(path).toString())[0];
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if(obj.getClass() == TrackEntry.class) {
			return ((TrackEntry) obj).path.equals(this.path);
		}
		return super.equals(obj);
	}
	@Override
	public void createData(Map<String, DataType> data) {
		description = (DataString) data.get("Description");
		length = (DataInt) data.get("Length");
	}
}
