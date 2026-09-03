package org.mediaplayer.server;

import Tools.Core.Util.Multithreadable;
import Tools.Core.Util.Threadsystem;

public class ThreadsystemMock extends Threadsystem {

	public ThreadsystemMock() {
		super(0);
	}
	@Override
	public void multiThread(Multithreadable m) {
		m.threadFunction();
	}

}
