package Tools.Connection;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import Tools.Files.Util;
import Tools.Util.Threadsystem;

public abstract class Endpoint{
	Map<String, CompletableFuture<String>> pending;
	Threadsystem threadSystem;
	public Endpoint(Threadsystem system) {
		this.threadSystem = system;
		this.pending = new ConcurrentHashMap<>();
	}

	public abstract void onOpen();
	public abstract void onInstruction(String s);
	public abstract String onRequest(String s);
	public abstract void write(String s);
	public abstract void onClose(int statusCode, String reason);
	public void onAnswer(String s) {
		String id = Util.getSign(s);
		s = s.substring(id.length()+1);
		pending.remove(id).complete(s);
	}
	public void onText(String text) {
		System.out.println("onText: " + text);
		threadSystem.multiThread(() -> {
			String sign = Util.getSign(text);
			System.out.println("sign: " + sign);
			if(sign == null) return;
            switch(sign) {
                case "I" -> onInstruction(text.substring(2));
                case "R" -> answerClientRequest(text.substring(2));
                case "A" -> onAnswer(text.substring(2));
            }
		});
	}
	public void answerClientRequest(String s) {
		String id = Util.getSign(s);
		s = s.substring(id.length()+1);

		System.out.println("Answer: " + id + ":" + s);
		String answer = onRequest(s);

		if(answer == null) answer = "0";
		write("A:" + id + ":" + answer);
	}
	public void instruct(String instruction) {
		write("I:" + instruction);
	}
	public String request(String request) throws ExecutionException, InterruptedException {
		String id = UUID.randomUUID().toString();
		write("R:" + id + ":" + request);

		CompletableFuture<String> future = new CompletableFuture<>();
		pending.put(id, future);
		return future.get();
	}
	
}