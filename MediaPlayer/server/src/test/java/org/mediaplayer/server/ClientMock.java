package org.mediaplayer.server;

import org.mediaplayer.Tools.Connection.WebServer.ServerSideConnection;
import org.mediaplayer.core.Interfaces.ClientConnectionManager;

import Tools.Core.Util.Threadsystem;
import Tools.Desktop.Connection.DesktopClient;


public class ClientMock extends DesktopClient {
	ServerSideConnection serverSideConnection;
	OnSend onSend;
	public ClientMock(Threadsystem threadSystem, ClientConnectionManager connectionManager) throws Exception {
		super("", 0, "", threadSystem, connectionManager);
	}
	public void setServer(ServerSideConnection serverSideConnection, OnSend onSend) {
		this.serverSideConnection = serverSideConnection;
		this.onSend = onSend;
	}
	@Override
	protected void create(String host, int port, String name) throws Exception {
		
	}
	@Override
	public void connect() {
		
	}
	@Override
	public void write(String s) {
		System.out.println();
		System.out.println("ClientWrite");
		onSend.onText(s);
		serverSideConnection.onText(s);
	}

}
