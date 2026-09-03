package Tools.Core.Connection;

import java.util.concurrent.ExecutionException;

public interface Connactable {
	void connect();
	void close();
	void instruct(String s);
	String request(String s) throws ExecutionException, InterruptedException;
}
