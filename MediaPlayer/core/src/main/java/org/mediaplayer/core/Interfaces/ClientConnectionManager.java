package org.mediaplayer.core.Interfaces;

public interface ClientConnectionManager {
	void connectionCreated();
	void onDisconnect(int statusCode, String reason);
	void followInstruction(String request);
	String answerRequest(String request);

}
