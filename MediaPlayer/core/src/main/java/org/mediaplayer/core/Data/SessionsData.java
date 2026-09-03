package org.mediaplayer.core.Data;

import java.util.HashMap;
import java.util.Map;

import Tools.Core.Files.Data.DataAdapter;
import Tools.Core.Files.Data.DataType;
import Tools.Core.Files.Data.DataTypes.DataMap;
import Tools.Core.Files.Data.DataTypes.DataString;

public class SessionsData extends DataAdapter {

	public Map<DataString, DataString> sessions;
	public DataString currentSession;
	public DataMap<DataString, DataString> dataMap;

	public SessionsData() {
		sessions = new HashMap<>();
		currentSession = new DataString();
		dataMap = new DataMap<>(DataString::new, DataString::new, sessions);
	}

	@Override
	public void createMapping(Map<String, DataType> data) {
		data.put("Sessions", dataMap);
		data.put("Current", currentSession);
	}
}
