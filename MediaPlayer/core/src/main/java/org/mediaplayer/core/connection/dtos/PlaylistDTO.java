package org.mediaplayer.core.connection.dtos;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.mediaplayer.core.DataPlaylistEntry;
import org.mediaplayer.core.Playlist;

import Tools.Core.Files.Data.DataClass;
import Tools.Core.Files.Data.DataTypes.DataArray;
import Tools.Core.Files.Data.DataTypes.DataString;

public class PlaylistDTO extends DataClass {
	private DataString path;
	private DataString id;
	private List<DataPlaylistEntry> playlist;
	private List<DataString> subplaylists;
	public PlaylistDTO() {
		setup(new DataString(), new DataString(), new ArrayList<>(),  new ArrayList<>());
	}
	public PlaylistDTO(Playlist playlist) {
		this(new DataString(playlist.path.toString()), new DataString(playlist.id), playlist.get(),playlist.getSubPlaylists());
	}
	public PlaylistDTO(DataString path, DataString id, List<DataPlaylistEntry> playlist, List<DataString> subplaylists) {
		setup(path, id, playlist, subplaylists);
		created = true;
	}
	private void setup(DataString path, DataString id, List<DataPlaylistEntry> playlist, List<DataString> subplaylists) {
		this.path = path;
		this.id = id;
		this.playlist = playlist;
		this.subplaylists = subplaylists;
		
		createData(path, id,
				new DataArray<DataPlaylistEntry>(DataPlaylistEntry::new, playlist),
				new DataArray<DataString>(DataString::new, subplaylists));
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
	public List<String> getSubplaylists() {
		return subplaylists.stream().map((ds) -> ds.get()).toList();
	}
}
