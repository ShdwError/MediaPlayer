package org.mediaplayer.server;

import java.io.IOException;

import org.mediaplayer.server.MediaPlayerServer;

import Tools.Core.Files.Data.Exceptions.DataTypeException;
import Tools.Desktop.Connection.DesktopClient;

public class ServerMock extends MediaPlayerServer {

	public ServerMock() throws IOException, DataTypeException {
		super("", 0, "", new FileManagerMock(), new ThreadsystemMock());
	}
	public void addConnection(ClientMock client, OnSend onSend) {
		ServerSideConnection connection = new ServerSideConnection(threadSystem, null) {
			@Override
			public void write(String s) {
				System.out.println();
				System.out.println("ServerWrite");
				onSend.onText(s);
				client.onText(s);
			}
		};
		client.setServer(connection, onSend);
		connections.add(connection);
	}
	@Override
	protected void createServer(String host, int port, String name) {
		
	}
	@Override
	public void run() throws Exception {
		
	}

}
