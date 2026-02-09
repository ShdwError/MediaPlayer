package org.MediaPlayer.Data;

import java.util.Map;

import Tools.Files.Data.DataAdapter;
import Tools.Files.Data.DataType;
import Tools.Files.Data.DataTypes.DataMap;
import Tools.Files.Data.DataTypes.DataString;

public class PlaylistsData extends DataAdapter {

	public Map<String, DataString> playlists;
	@Override
	public void createData(Map<String, DataType> data) {
		playlists = ((DataMap<DataString>) data.get("Playlists")).get();
		
	}

}
