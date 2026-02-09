package org.MediaPlayer;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.MediaPlayer.DataTypes.DataPlaylistEntry;

import Tools.Files.Data.DataAdapter;
import Tools.Files.Data.DataType;
import Tools.Files.Data.Util;
import Tools.Files.Data.DataTypes.*;

public class Playlist extends DataAdapter {
	Path path;
	public String id;
	public DataString description;
	private List<DataPlaylistEntry> playlist;
	public List<DataString> subplaylists;
	public Set<DataPlaylistEntry> uniqueSet;
	
	public Playlist(Path path, String id) {
		this.path = path;
		this.id = id;
		this.uniqueSet = new HashSet<>();
	}
	public String getName() {
		return Util.getNameAndType(Path.of("Playlists").relativize(path).toString())[0];
	}
	public void add(DataPlaylistEntry entry) {
		if(!uniqueSet.contains(entry)) {
			uniqueSet.add(entry);
			playlist.add(entry);
		}
	}
	public void add(List<DataPlaylistEntry> entries) {
		for(DataPlaylistEntry entry: entries) {
			add(entry);
		}
	}
	public void add(Playlist playlist)  {
		add(playlist.playlist);
	}
	@SuppressWarnings("unlikely-arg-type")
	public void remove(TrackEntry entry) {
		playlist.remove(entry);
	}
	public void set(int i, int j) {
		playlist.set(i, get(j));
	}
	public void set(int i, DataPlaylistEntry entry) {
		playlist.set(i, entry);
	}
	public DataPlaylistEntry get(int i) {
		return playlist.get(i);
	}
	//Is that smart?
	public List<DataPlaylistEntry> getAll(Map<String, Playlist> playlists) {
		List<DataPlaylistEntry> ret = new ArrayList<>(playlist);
		for(DataString ds: subplaylists) {
			Playlist subplaylist = playlists.getOrDefault(ds.get(), null);
			if(subplaylist != null)
				ret.addAll(subplaylist.getAll(playlists));
		}
		return ret;
	}
	public int size() {
		return playlist.size();
	}
	public int fullSize(Map<String, Playlist> playlists) {
		int size = size();
		for(DataString ds: subplaylists) {
			Playlist subplaylist = playlists.getOrDefault(ds.get(), null);
			if(subplaylist != null)
				size += subplaylist.fullSize(playlists);
		}
		return size;
	}
	
	//For Subclasses
	protected void createPlaylist(List<DataPlaylistEntry> playlist) {
		this.playlist = playlist;
	}
	@Override
	public void createData(Map<String, DataType> data) {
		this.description = (DataString) data.get("Description");
		createPlaylist(((DataArray<DataPlaylistEntry>) data.get("Playlist")).get());
		this.subplaylists = ((DataArray<DataString>) data.get("Subplaylists")).get();
	}

}
