package org.mediaplayer.core.connection.messages;

import Tools.Core.Files.Data.DataTypedClass;
import Tools.Core.Files.Data.DataTypes.DataString;

public class Message extends DataTypedClass<GenericMessage> {
	public Message() {
		super();
	}
	public Message(String type, GenericMessage message) {
		super(new DataString(type), message);
	}
	@Override
	public void createTypes() {
		addType("change", Change::new);
		addType("sync", Sync::new);
		addType("request", Request::new);
	}

}
