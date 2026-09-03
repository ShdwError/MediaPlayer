package org.mediaplayer.core.connection.messages;

import org.mediaplayer.core.connection.messages.requests.SyncFromRequest;

import Tools.Core.Files.Data.DataTypedClass;
import Tools.Core.Files.Data.DataTypes.DataString;

public class RequestType extends DataTypedClass<GenericRequest> {
	public RequestType() {
		super();
	}
	public RequestType(String type, GenericRequest request) {
		super(new DataString(type), request);
	}
	@Override
	public void createTypes() {
		addType("syncFrom", SyncFromRequest::new);
	}

}
