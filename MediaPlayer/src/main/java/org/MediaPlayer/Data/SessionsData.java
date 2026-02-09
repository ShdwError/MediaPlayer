package org.MediaPlayer.Data;

import java.util.Map;

import Tools.Files.Data.DataAdapter;
import Tools.Files.Data.DataType;
import Tools.Files.Data.DataTypes.DataMap;
import Tools.Files.Data.DataTypes.DataString;

public class SessionsData extends DataAdapter {

	public Map<String, DataString> sessions;
	public DataString currentSession;
	@Override
	public void createData(Map<String, DataType> data) {
		sessions = ((DataMap<DataString>) data.get("Sessions")).get();
		currentSession = (DataString) data.get("Current");
	}

}
