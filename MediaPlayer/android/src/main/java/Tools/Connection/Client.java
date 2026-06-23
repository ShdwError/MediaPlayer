package Tools.Connection;

import Tools.Util.Threadsystem;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public abstract class Client {
	Threadsystem threadSystem;
	protected ClientSideConnection connection;
	private WebSocket webSocket;
	public Client(String host, int port, String name) {
		init();

		OkHttpClient client = new OkHttpClient();

		Request request = new Request.Builder()
						.url("ws://" + host + ":" + port + "/" + name)
						.build();

		webSocket = client.newWebSocket(request, new WebSocketListener() {
			@Override
			public void onOpen(WebSocket webSocket, Response response) {
				connection = new ClientSideConnection(threadSystem);
				connection.onOpen();
			}
			@Override
			public void onMessage(WebSocket webSocket, String text) {
				connection.onText(text);
			}
			@Override
			public void onClosed(WebSocket webSocket, int code, String reason) {
				connection.onClose(code, reason);
			}
		});
	}
	public void init() {
		threadSystem = new Threadsystem(4);
	}

	public abstract void connectionCreated(ClientSideConnection connection);
	public void connectionDisconnected() {
		this.connection = null;
		this.webSocket = null;
	}
	public abstract void followInstruction(String request);
	public abstract String answerRequest(String request);
	
	public class ClientSideConnection extends Endpoint {
		public ClientSideConnection(Threadsystem system) {
			super(system);
		}
		@Override
		public void onOpen() {
			System.out.println("Client onOpen");
			connectionCreated(this);
		}
		@Override
		public void onInstruction(String s) {
			followInstruction(s);
		}
		@Override
		public String onRequest(String s) {
			return answerRequest(s);
		}

		@Override
		public void write(String s) {
			webSocket.send(s);
		}
		@Override
		public void onClose(int statusCode, String reason) {
			connectionDisconnected();
		}
	}
}