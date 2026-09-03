package Tools.Connection;

import Tools.Util.Threadsystem;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public abstract class Client {
	private Threadsystem threadSystem;
	
	private OkHttpClient httpClient;
	private WebSocket webSocket;
	protected ClientSideConnection connection;
	
	private ClientConnectionManager connectionManager;
	
	private final String host;
    private final int port;
    private final String name;

    private boolean closed;
	
	public Client(String host, int port, String name, ClientConnectionManager connectionManager) {
		this.threadSystem = new Threadsystem(getThreadAmount());
		
		this.host = host;
        this.port = port;
        this.name = name;

        this.httpClient = new OkHttpClient();
	}
	
	public void connect() {
		Request request = new Request.Builder()
				.url("ws://" + host + ":" + port + "/" + name)
				.build();

		webSocket = httpClient.newWebSocket(request, new WebSocketListener() {
			@Override
			public void onOpen(WebSocket webSocket, Response response) {
				connection = new ClientSideConnection(threadSystem);
				connection.onOpen();
			}
			@Override
			public void onMessage(WebSocket webSocket, String text) {
				threadSystem.multiThread(() -> {
					connection.onText(text);
				});
			}
			@Override
			public void onClosed(WebSocket webSocket, int code, String reason) {
				connection.onClose(code, reason);
			}
			@Override
			public void onFailure(WebSocket webSocket, Throwable t, Response response) {
			    if(connection != null)
			    	connection.onClose(-1, t.getMessage());
			}
		});
	}
	
	private void connectionDisconnected(int statusCode, String reason) {
		if(connection != null) {
			connection.onClose(code, reason);
			connection = null;
        }
		
        if(!closed) {
            reconnect();
        }
	}
	private void reconnect() {
        threadSystem.multiThread(() -> {
            try {
                Thread.sleep(3000);
                connect();
            }
            catch(InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    public void close() {
        closed = true;

        if(webSocket != null)
            webSocket.close(1000, "Client closed");
    }

	public abstract int getThreadAmount();
	
	public abstract void connectionCreated();
	public abstract void followInstruction(String request);
	public abstract String answerRequest(String request);
	
	public class ClientSideConnection extends Endpoint {
		public ClientSideConnection(Threadsystem system) {
			super(system);
		}
		@Override
		public void onOpen() {
			connectionCreated();
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