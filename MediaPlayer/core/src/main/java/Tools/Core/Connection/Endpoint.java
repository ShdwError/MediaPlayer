package Tools.Core.Connection;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import Tools.Core.Files.Util;
import Tools.Core.Util.Threadsystem;

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
		String[] split = Util.split(s);
		String id = split[0];
		s = split[1];
		pending.remove(id).complete(s);
	}
	public void onText(String text) {
		System.out.println("onText: " + text);
		threadSystem.multiThread(() -> {
			String[] split = Util.split(text);
			if(split == null)
				return;
			String sign = split[0];
            switch(sign) {
                case "I" -> onInstruction(split[1]);
                case "R" -> answerClientRequest(split[1]);
                case "A" -> onAnswer(split[1]);
            }
		});
	}
	public void answerClientRequest(String s) {
		String[] split = Util.split(s);
		if(split == null)
			return;
		String id = split[0];
		s = split[1];

		String answer = onRequest(s);
		System.out.println("Answer: " + answer);

		if(answer == null) answer = "0";
		write("A:" + id + ":" + answer);
	}
	public void instruct(String instruction) {
		write("I:" + instruction);
	}
	public String request(String request) throws ExecutionException, InterruptedException {
		String id = UUID.randomUUID().toString();
		
		CompletableFuture<String> future = new CompletableFuture<>();
		pending.put(id, future);
		
		write("R:" + id + ":" + request);

		return future.get();
	}
	
}