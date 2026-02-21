package org.MediaPlayer;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.MediaPlayer.DataTypes.DataPlaylistEntry;

import Tools.Files.Util;
import Tools.Files.Data.DataAdapter;
import Tools.Files.Data.DataType;
import Tools.Files.Data.DataTypes.*;

public class Playlist extends DataAdapter {
	Path path;
	public String id;
	public DataString description;
	private List<DataPlaylistEntry> playlist;
	private List<DataString> subplaylists;
	public Set<String> uniqueEntrySet;
	public Set<String> uniquePlaylistSet;
	
	public Playlist(Path path, String id) {
		this.path = path;
		this.id = id;
		this.uniqueEntrySet = new HashSet<>();
		this.uniquePlaylistSet = new HashSet<>();
		this.subplaylists = new ArrayList<>();
		this.playlist = new ArrayList<>();
	}
	public String getName() {
		return Util.getNameAndType(Path.of("Playlists").relativize(path).toString())[0];
	}
	
	public void add(DataPlaylistEntry entry) {
		if(!uniqueEntrySet.contains(entry.id.get())) {
			uniqueEntrySet.add(entry.id.get());
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
	public boolean remove(TrackEntry entry) {
		return playlist.remove(entry);
	}
	public void remove(DataPlaylistEntry dpe) {
		playlist.remove(dpe);
	}
	public void remove(int i) {
		playlist.remove(i);
	}
	public void set(int i, DataPlaylistEntry entry) {
		playlist.set(i, entry);
	}
	public void set(List<DataPlaylistEntry> playlist) {
		this.playlist.clear();
		this.playlist.addAll(playlist);
	}
	public DataPlaylistEntry get(int i) {
		return playlist.get(i);
	}
	public List<DataPlaylistEntry> get() {
		return playlist;
	}
	public String getId(int i) {
		return playlist.get(i).id.get();
	}
	public List<DataPlaylistEntry> getAll(Map<String, Playlist> playlists, Set<String> newUniqueSet, boolean getCopy) {
		List<DataPlaylistEntry> ret = new ArrayList<>(playlist);
		for(DataString ds: subplaylists) {
			Playlist subplaylist = playlists.get(ds.get());
			if(subplaylist != null) {
				for(DataPlaylistEntry dpe: subplaylist.getAll(playlists, newUniqueSet, getCopy)) {
					if(!newUniqueSet.contains(dpe.id.get())) {
						newUniqueSet.add(dpe.id.get());
						if(getCopy)
							ret.add(dpe.copy());
						else ret.add(dpe);
					}
				}
			}
		}
		return ret;
	}
	
	public void addSubplaylist(String id) {
		if(!uniquePlaylistSet.contains(id)) {
			uniquePlaylistSet.add(id);
			subplaylists.add(new DataString(id));
		}
	}
	public int size() {
		return playlist.size();
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
	
	public List<DataPlaylistEntry> reorganize() {
		Map<String, DataPlaylistEntry> entryMap = new HashMap<>();
		
		boolean hasForced = false;
	    Set<String> forcedTargets = new HashSet<>();
	    
	    for (DataPlaylistEntry e : get()) {
	        entryMap.put(e.id.get(), e);
	        if(e.getForcedNext() != null) {
	        	hasForced = true;
	            forcedTargets.add(e.getForcedNext());
	        }
	    }
	    if(!hasForced) return get();

	    Set<String> visited = new HashSet<>();
	    List<DataPlaylistEntry> ordered = new ArrayList<>(); 
	    
	    for(DataPlaylistEntry dpe : get()) {
	    	if(forcedTargets.contains(dpe.id.get())) continue;
	        if(visited.contains(dpe.id.get())) continue;

	        DataPlaylistEntry current = dpe;
	        while (current != null && !visited.contains(current.id.get())) {
	            ordered.add(current);
	            visited.add(current.id.get());

	            String nextId = current.getForcedNext();
	            if (nextId != null && entryMap.containsKey(nextId)) {
	                current = entryMap.get(nextId);
	            } 
	            else {
	                current = null;
	            }
	        }
	    }
	    if(visited.size() < entryMap.size()) {
	    	for(DataPlaylistEntry dpe : get()) {
	    	    if(!visited.contains(dpe.id.get())) {
	    	        ordered.add(dpe);
	    	    }
	    	}
	    }
	    set(ordered);
	    return ordered;
	}
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Playlist p) {
			return p.id.equals(this.id);
		}
		return super.equals(obj);
	}

}
