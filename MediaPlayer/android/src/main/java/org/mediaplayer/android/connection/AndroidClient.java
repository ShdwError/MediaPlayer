package org.mediaplayer.android.connection;

import Tools.Connection.GenericAndroidClient;

public class AndroidClient extends GenericAndroidClient {
    public AndroidClient(String host, int port, String name) {
        super(host, port, name);
    }

    @Override
    public void connectionCreated(ClientSideConnection connection) {

    }
    @Override
    public void followInstruction(String request) {

    }
    @Override
    public String answerRequest(String request) {
        return "";
    }
}
