package org.mediaplayer.core.Data;

import java.util.HashMap;
import java.util.Map;

import Tools.Files.Data.DataAdapter;
import Tools.Files.Data.DataType;
import Tools.Files.Data.DataTypes.DataMap;
import Tools.Files.Data.DataTypes.DataString;

public class PlaylistsData extends DataAdapter {

	public Map<String, DataString> playlists;
	public PlaylistsData() {
		playlists = new HashMap<>();
	}

	@Override
	public void createMapping(Map<String, DataType> data) {
		data.put("Playlists", new DataMap<>(DataString::new, playlists));
	}
}
