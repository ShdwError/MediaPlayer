package org.mediaplayer.core.Data;

import java.util.HashMap;
import java.util.Map;

import Tools.Core.Files.Data.DataAdapter;
import Tools.Core.Files.Data.DataType;
import Tools.Core.Files.Data.DataTypes.*;

public class SoundtracksData extends DataAdapter {
	public Map<DataString, DataString> soundtracks;
	public DataMap<DataString, DataString> dataMap;
	public SoundtracksData() {
		soundtracks = new HashMap<>();
		dataMap = new DataMap<>(DataString::new, DataString::new, soundtracks);
	}
	@Override
	public void createMapping(Map<String, DataType> data) {
		data.put("Soundtracks", dataMap);
	}
}
