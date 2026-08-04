package org.mediaplayer.android.connection;

import Tools.Connection.Client;

public class AndroidClient extends Client {
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
