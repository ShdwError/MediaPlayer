package org.MediaPlayer.Data;

import java.util.Map;

import Tools.Files.Data.DataAdapter;
import Tools.Files.Data.DataType;
import Tools.Files.Data.DataTypes.*;

public class SoundtracksData extends DataAdapter {
	public Map<String, DataString> soundtracks;
	@Override
	public void createData(Map<String, DataType> data) {
		soundtracks = ((DataMap<DataString>) data.get("Soundtracks")).get();
		
	}

}
