package org.mediaplayer.core;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Tools.Core.Files.Util;
import Tools.Core.Files.Data.DataAdapter;
import Tools.Core.Files.Data.DataType;
import Tools.Core.Files.Data.DataTypes.*;

public class Playlist extends DataAdapter {
	public Path path;
	public String id;
	public DataString description;
	private DataBoolean shuffle; //IDK what dis does
	private List<DataPlaylistEntry> playlist;
	private List<DataString> subplaylists;
	public Set<String> uniqueEntrySet;
	public Set<String> uniquePlaylistSet;
	
	public Playlist(Path path, String id) {
		this.path = path;
		this.id = id;
		this.description = new DataString();
		this.shuffle = new DataBoolean();
		this.playlist = new ArrayList<>();
		this.subplaylists = new ArrayList<>();
		this.uniqueEntrySet = new HashSet<>();
		this.uniquePlaylistSet = new HashSet<>();
	}
	public Playlist(Path path, List<DataPlaylistEntry> playlist, List<String> subplaylists, String id) {
		this.path = path;
		this.id = id;
		this.description = new DataString(); //TODO: in DTO?
		this.shuffle = new DataBoolean();
		this.playlist = new ArrayList<>();
		this.subplaylists = new ArrayList<>();
		this.uniqueEntrySet = new HashSet<>();
		this.uniquePlaylistSet = new HashSet<>();
		
		this.add(playlist);
		this.addSubplaylists(subplaylists);
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
	public void addAt(int pos, DataPlaylistEntry entry) {
		if(!uniqueEntrySet.contains(entry.id.get())) {
			uniqueEntrySet.add(entry.id.get());
			playlist.add(pos, entry);
		}
	}
	@SuppressWarnings("unlikely-arg-type")
	public boolean remove(TrackEntry entry) {
		uniqueEntrySet.remove(entry.id);
		return playlist.remove(entry);
	}
	public boolean remove(DataPlaylistEntry dpe) {
		uniqueEntrySet.remove(dpe.id.get());
		return playlist.remove(dpe);
	}
	public DataPlaylistEntry remove(int i) {
		DataPlaylistEntry dpe = playlist.remove(i);
		uniqueEntrySet.remove(dpe.id.get());
		return dpe;
	}
	public void set(int i, DataPlaylistEntry entry) {
		if(!uniqueEntrySet.contains(entry.id.get())) {
			uniqueEntrySet.add(entry.id.get());
			playlist.set(i, entry);
		}
	}
	public void set(List<DataPlaylistEntry> playlist) {
		this.playlist.clear();
		this.add(playlist);
	}
	public DataPlaylistEntry get(int i) {
		return playlist.get(i);
	}
	public DataPlaylistEntry get(String id) {
		for(DataPlaylistEntry dpe: playlist)  {
			if(dpe.id.get().equals(id)) return dpe;
		}
		return null;
	}
	public List<DataPlaylistEntry> get() {
		return playlist;
	}
	public String getId(int i) {
		return playlist.get(i).id.get();
	}
	public List<DataPlaylistEntry> getAll(Map<String, Playlist> playlists, boolean getCopy) {
		List<DataPlaylistEntry> ret = new ArrayList<>(playlist);
		Set<String> newUniqueSet = new HashSet<>(uniqueEntrySet);
		for(DataString ds: subplaylists) {
			Playlist subplaylist = playlists.get(ds.get());
			if(subplaylist != null) {
				for(DataPlaylistEntry dpe: subplaylist.getAll(playlists, getCopy)) {
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
	
	public List<DataString> getSubPlaylists() {
		return subplaylists;
	}
	public void addSubplaylist(String id) {
		if(!uniquePlaylistSet.contains(id)) {
			uniquePlaylistSet.add(id);
			subplaylists.add(new DataString(id));
		}
	}
	public void addSubplaylists(List<String> subplaylists) {
		for(String id: subplaylists) {
			addSubplaylist(id);
		}
	}
	public void removeSubplaylist(int pos) {
		String id = subplaylists.get(pos).get();
		uniquePlaylistSet.remove(id);
		subplaylists.remove(pos);
	}
	public boolean removeSubplaylist(String id) {
		uniquePlaylistSet.remove(id);
		return subplaylists.remove(new DataString(id));
	}
	public int size() {
		return playlist.size();
	}
	
	public void create(List<DataPlaylistEntry> playlist) {
		this.playlist = playlist;
		for(DataPlaylistEntry dpe: playlist) {
			if(!uniqueEntrySet.contains(dpe.id.get()))
				uniqueEntrySet.add(dpe.id.get());
		}
	}
	
	public List<DataPlaylistEntry> reorganize() {
		Map<String, DataPlaylistEntry> entryMap = new HashMap<>();
		
		boolean hasForced = false;
	    Set<String> forcedTargets = new HashSet<>();
	    
	    for(DataPlaylistEntry e : get()) {
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
	    	if(forcedTargets.contains(dpe.id.get()) || visited.contains(dpe.id.get()))
				continue;

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
	@Override
	public String toString() {
		return getName();
	}

	@Override
	public void createMapping(Map<String, DataType> data) {
		data.put("Description", description);
		data.put("Playlist", new DataArray<>(DataPlaylistEntry::new, playlist));
		data.put("Subplaylists", new DataArray<>(DataString::new, subplaylists));
	}
}
