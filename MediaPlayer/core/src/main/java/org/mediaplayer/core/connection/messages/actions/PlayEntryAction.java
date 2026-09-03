package org.mediaplayer.core.connection.messages.actions;

import java.time.LocalDateTime;

import org.mediaplayer.core.connection.messages.ActionKey;
import org.mediaplayer.core.connection.messages.GenericAction;

import Tools.Core.Files.Data.DataTypes.DataDate;
import Tools.Core.Files.Data.DataTypes.DataString;

public class PlayEntryAction extends GenericAction {
	private DataString sessionId;
	private DataString entryId;
	public PlayEntryAction()  {
		this.date = new DataDate();
		this.sessionId = new DataString();
		this.entryId = new DataString();
		
		createData(date, sessionId, entryId);
	}
	public PlayEntryAction(LocalDateTime dateTime, String sessionId, String entryId) {
		this(new DataDate(dateTime), new DataString(sessionId), new DataString(entryId));
	}
	public PlayEntryAction(DataDate date, DataString sessionId, DataString entryId) {
		this.date = date;
		this.sessionId = sessionId;
		this.entryId = entryId;
		
		createData(date, sessionId, entryId);
		created = true;
	}
	@Override
	public ActionKey getKey() {
		return new ActionKey(getDataType());
	}
	public String getSessionId() {
		return sessionId.get();
	}
	public String getEntryId() {
		return entryId.get();
	}
	
	
}
