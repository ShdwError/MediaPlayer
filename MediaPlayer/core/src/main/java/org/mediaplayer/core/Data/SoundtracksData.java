package org.mediaplayer.core.Data;

import java.util.HashMap;
import java.util.Map;

import Tools.Files.Data.DataAdapter;
import Tools.Files.Data.DataType;
import Tools.Files.Data.DataTypes.*;

public class SoundtracksData extends DataAdapter {
	public Map<String, DataString> soundtracks;
	public SoundtracksData() {
		soundtracks = new HashMap<>();
	}
	@Override
	public void createMapping(Map<String, DataType> data) {
		data.put("Soundtracks", new DataMap<>(DataString::new, soundtracks));
	}
}
