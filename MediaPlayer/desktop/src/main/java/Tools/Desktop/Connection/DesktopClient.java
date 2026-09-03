package Tools.Desktop.Connection;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.websocket.api.Callback;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.mediaplayer.core.Interfaces.ClientConnectionManager;

import Tools.Core.Connection.Connactable;
import Tools.Core.Connection.Endpoint;
import Tools.Core.Util.Threadsystem;

public class DesktopClient extends Endpoint implements Connactable {
	private Threadsystem threadSystem;
	private ClientConnectionManager connectionManager;
	
	private HttpClient httpClient;
	private WebSocketClient webSocketClient;
	private URI serverURI;
	
	private ClientStatus status;
	private Session session;
	public DesktopClient(String host, int port, String name, Threadsystem threadSystem, ClientConnectionManager connectionManager) throws Exception {
		super(threadSystem);
		this.threadSystem = threadSystem;
		this.connectionManager = connectionManager;
		this.status = ClientStatus.WAITING;
		
		create(host, port, name);
	}
	protected void create(String host, int port, String name) throws Exception {
		this.httpClient = new HttpClient();
		this.webSocketClient = new WebSocketClient(httpClient);
		
		webSocketClient.start();
		
		this.serverURI = URI.create("ws://" + host + ":" + port + "/" + name);
	}
	@Override
	public void connect() {
		if(status != ClientStatus.WAITING) 
			return;
		try {
			status = ClientStatus.CONNECTING;
			webSocketClient.connect(new Session.Listener() {
				@Override
				public void onWebSocketOpen(Session session) {
					status = ClientStatus.CONNECTED;
					setSession(session);
					threadSystem.multiThread(() -> {
						onOpen();
					});
					session.demand();
				}
				@Override
				public void onWebSocketText(String text) {
					if(session == null)
						return;
					onText(text);
					session.demand();
				}
				@Override
				public void onWebSocketBinary(ByteBuffer payload, Callback callback) {
					callback.succeed();
					if(session == null)
						return;
					session.demand();
				}
				@Override
				public void onWebSocketClose(int statusCode, String reason) {
					threadSystem.multiThread(() -> {
						onClose(statusCode, reason);
					});
				}
			}, serverURI);
		}
		catch(Exception e) {
			onClose(-1, e.getMessage());
		}
	}
	private void setSession(Session session) {
		this.session = session;
	}
	@Override
	public void close() {
		status = ClientStatus.CLOSED;
		
		if(session != null)
			session.close();
		
		session = null;
	}
	@Override
	public void onOpen() {
		connectionManager.connectionCreated();
	}
	@Override
	public void onClose(int statusCode, String reason) {
		this.session = null;
		this.status = ClientStatus.WAITING;
		connectionManager.onDisconnect(statusCode, reason);
	}
	@Override
	public void onInstruction(String request) {
		connectionManager.followInstruction(request);
	}
	@Override
	public String onRequest(String request) {
		return connectionManager.answerRequest(request);
	}
	@Override
	public void write(String s) {
		if(session == null || s == null)
			return;
		session.sendText(s, Callback.from(session::demand, Throwable::printStackTrace));
	}
	public enum ClientStatus {
		WAITING,
		CONNECTED,
		CONNECTING,
		CLOSED
	}
}
