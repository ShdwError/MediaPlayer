package org.mediaplayer.core.connection.messages.requests;

import org.mediaplayer.core.connection.messages.GenericRequest;

import Tools.Core.Files.Data.DataTypes.DataInt;

public class SyncFromRequest extends GenericRequest {
	private DataInt from;
	public SyncFromRequest() {
		this.from = new DataInt();
		createData(from);
	}
	public SyncFromRequest(DataInt from) {
		this.from = from;
		createData(from);
		created = true;
	}
	public int getFromVersion() {
		return from.get();
	}

}
