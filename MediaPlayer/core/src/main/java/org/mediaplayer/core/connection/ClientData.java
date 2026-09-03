package org.mediaplayer.core.connection;

import java.util.Map;

import Tools.Core.Files.Data.DataAdapter;
import Tools.Core.Files.Data.DataType;
import Tools.Core.Files.Data.DataTypes.DataInt;

public class ClientData extends DataAdapter {
	public DataInt version;
	public ClientData() {
		version = new DataInt(0);
	}
	@Override
	public void createMapping(Map<String, DataType> data) {
		data.put("version", version);
	}

}
