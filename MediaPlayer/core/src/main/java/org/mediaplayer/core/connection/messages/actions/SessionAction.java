package org.mediaplayer.core.connection.messages.actions;

import java.time.LocalDateTime;

import org.mediaplayer.core.connection.dtos.SessionDTO;
import org.mediaplayer.core.connection.messages.ActionKey;
import org.mediaplayer.core.connection.messages.GenericAction;

import Tools.Core.Files.Data.DataClass;
import Tools.Core.Files.Data.DataTypes.*;

public class SessionAction extends GenericAction {
	private SessionDTO session;
	public SessionAction() {
		this.date = new DataDate();
		this.session = new SessionDTO();
		
		createData(date, session);
	}
	public SessionAction(LocalDateTime dateTime, SessionDTO session) {
		this(new DataDate(dateTime), session);
	}
	public SessionAction(DataDate date, SessionDTO session) {
		this.date = date;
		this.session = session;
		
		createData(date, session);
		created = true;
	}
	public SessionDTO get() {
		return session;
	}
	@Override
	public ActionKey getKey() {
		return new ActionKey(session.getId());
	}
	

}
