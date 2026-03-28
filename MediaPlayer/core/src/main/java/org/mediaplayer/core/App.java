package org.mediaplayer.core;

import java.io.IOException;
import java.nio.file.Path;

import org.mediaplayer.core.Generics.GenericAudio;
import org.mediaplayer.core.Generics.GenericFileLogic;
import org.mediaplayer.core.Generics.GenericUI;

public class App {
	
	public GenericFileLogic fileLogic;
	public GenericUI ui;
	public GenericAudio audio;
	
	public App(Path path) throws IOException {
	}

}
