package org.mediaplayer.core.connection.dtos;

import java.nio.file.Path;

import org.mediaplayer.core.TrackEntry;

import Tools.Core.Files.Data.DataClass;
import Tools.Core.Files.Data.DataTypes.DataInt;
import Tools.Core.Files.Data.DataTypes.DataString;

public class TrackEntryDTO extends DataClass {
	private DataString path;
	private DataString description;
	private DataInt length;
	public TrackEntryDTO() {
		this.path = new DataString();
		this.description = new DataString();
		this.length = new DataInt();
		createData(path, description, length);
	}
	public TrackEntryDTO(TrackEntry entry) {
		this(new DataString(entry.path.toString()), entry.description, entry.length);
	}
	public TrackEntryDTO(DataString path, DataString description, DataInt length) {
		this.description = description;
		this.length = length;
		this.path = path;
		createData(path, description, length);
		created = true;
	}
	public Path getPath() {
		return Path.of(path.get());
	}
	public DataString getDescription() {
		return description;
	}
	public DataInt getLength() {
		return length;
	}
}
