package org.mediaplayer.core.Data;

import java.util.HashMap;
import java.util.Map;

import Tools.Core.Files.Data.DataAdapter;
import Tools.Core.Files.Data.DataType;
import Tools.Core.Files.Data.DataTypes.DataMap;
import Tools.Core.Files.Data.DataTypes.DataString;

public class PlaylistsData extends DataAdapter {

	public Map<DataString, DataString> playlists;
	public DataMap<DataString, DataString> dataMap;
	public PlaylistsData() {
		playlists = new HashMap<>();
		dataMap = new DataMap<>(DataString::new, DataString::new, playlists);
	}

	@Override
	public void createMapping(Map<String, DataType> data) {
		data.put("Playlists", dataMap);
	}
}
