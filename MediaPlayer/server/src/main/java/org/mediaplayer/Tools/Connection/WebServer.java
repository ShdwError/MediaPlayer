package org.mediaplayer.Tools.Connection;

import java.nio.ByteBuffer;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.websocket.api.Callback;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.websocket.server.WebSocketUpgradeHandler;

import Tools.Core.Connection.Endpoint;
import Tools.Core.Util.Threadsystem;

public abstract class WebServer {
	protected Threadsystem threadSystem;
	protected Set<ServerSideConnection> connections;
	private Server server;
	
	public WebServer(String host, int port, String name, Threadsystem threadsystem) {
		this.threadSystem = threadsystem;
		connections = ConcurrentHashMap.newKeySet();
		createServer(host, port, name);
	}
	protected void createServer(String host, int port, String name) {
		server = new Server();
		
		ServerConnector connector = new ServerConnector(server); 
		connector.setHost(host); 
		connector.setPort(port); 
		server.addConnector(connector); 
		
		ContextHandler contextHandler = new ContextHandler(); 
		server.setHandler(contextHandler);
		
		WebSocketUpgradeHandler webSocketHandler = WebSocketUpgradeHandler.from(server, contextHandler, container -> {
			container.addMapping("/" + name, (rq, rs, cb) -> new Session.Listener() {
				ServerSideConnection connection;
				@Override
				public void onWebSocketOpen(Session session) {
					connection = new ServerSideConnection(threadSystem, session);
					threadSystem.multiThread(() -> {
						System.out.println("onOpen");
						connection.onOpen();
					});
					session.demand();
				}
				@Override
				public void onWebSocketText(String text) {
					connection.onText(text);
					connection.session.demand();
				}
				@Override
				public void onWebSocketBinary(ByteBuffer payload, Callback callback) {
					connection.session.demand();
				}
				@Override
				public void onWebSocketClose(int statusCode, String reason) {
					threadSystem.multiThread(() -> {
						connection.onClose(statusCode, reason);
					});
					connection.session.close();
				}
			});
		});
		contextHandler.setHandler(webSocketHandler);
	}
	
	public void run() throws Exception {
	    server.start();
	    server.join();
	}
	public void connectionDisconnected(ServerSideConnection connection) {
		connections.remove(connection);
	}
	public void connectionCreated(ServerSideConnection connection) {
		connections.add(connection);
	}
	public void broadcastInstruction(String s) {
		for(ServerSideConnection connection: connections) {
			connection.instruct(s);
		}
	}
	public void broadcastInstruction(String s, ServerSideConnection exception) {
		for(ServerSideConnection connection: connections) {
			if(connection != exception)
				connection.instruct(s);
		}
	}
	public abstract int getThreadAmount();
	
	public abstract void followInstruction(String request, ServerSideConnection connection);
	public abstract String answerRequest(String request, ServerSideConnection connection);
	
	
	public class ServerSideConnection extends Endpoint {
		private Session session;
		public ServerSideConnection(Threadsystem system, Session session) {
			super(system);
			this.session = session;
		}
		@Override
		public void onOpen() {
			connectionCreated(this);
		}
		@Override
		public void onInstruction(String s) {
			followInstruction(s, this);
		}
		@Override
		public String onRequest(String s) {
			return answerRequest(s, this);
		}
		@Override
		public void write(String s) {
			session.sendText(s, Callback.from(session::demand, Throwable::printStackTrace));
		}
		@Override
		public void onClose(int statusCode, String reason) {
			connectionDisconnected(this);
		}
	}
}
