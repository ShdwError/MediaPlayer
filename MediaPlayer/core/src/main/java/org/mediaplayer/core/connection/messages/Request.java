package org.mediaplayer.core.connection.messages;

public class Request extends GenericMessage {
	private RequestType type;
	public Request() {
		this.type = new RequestType();
		createData(type);
	}
	public Request(RequestType type) {
		this.type = type;
		createData(type);
		created = true;
	}
	public RequestType get() {
		return type;
	}

}
