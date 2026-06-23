package Tools.Util;

import java.util.Stack;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Threadsystem {
	Stack<Node> nodes;
	BlockingQueue<Multithreadable> queue;
	public Threadsystem(int threads) {
	queue = new LinkedBlockingQueue<>();
		nodes = new Stack<>();
		for(int i = 0; i < threads; i++) {
			Node node = new Node();
			node.start();
			nodes.push(node);
		}
	}
	public void multiThread(Multithreadable m) {
		queue.offer(m);
	}
	private class Node extends Thread {
		public Node() {

		}
		public void run() {
			while(true) {
				try {
					multiThread();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		public void multiThread() throws InterruptedException {
			queue.take().threadFunction();
		}
	}

}