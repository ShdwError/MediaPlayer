package org.mediaplayer.core.Data;

import java.util.HashMap;
import java.util.Map;

import Tools.Files.Data.DataAdapter;
import Tools.Files.Data.DataType;
import Tools.Files.Data.DataTypes.DataMap;
import Tools.Files.Data.DataTypes.DataString;

public class SessionsData extends DataAdapter {

	public Map<String, DataString> sessions;
	public DataString currentSession;

	public SessionsData() {
		sessions = new HashMap<>();
		currentSession = new DataString();
	}

	@Override
	public void createMapping(Map<String, DataType> data) {
		data.put("Sessions", new DataMap<>(DataString::new, sessions));
		data.put("Current", currentSession);
	}
}
