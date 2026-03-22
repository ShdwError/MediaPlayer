package org.mediaplayer.core;

import java.io.IOException;
import java.nio.file.Path;

import org.mediaplayer.core.Generics.GenericAudio;
import org.mediaplayer.core.Generics.GenericFileLogic;
import org.mediaplayer.core.Generics.GenericUI;
import org.mediaplayer.desktop.ConsoleUI;
import org.mediaplayer.desktop.DesktopAudio;
import org.mediaplayer.desktop.DesktopFileLogic;

public class App {
	
	public GenericFileLogic fileLogic;
	public GenericUI ui;
	public GenericAudio audio;
	
	public App(Path path) throws IOException {
		
		fileLogic = new DesktopFileLogic(path);
		ui = new ConsoleUI();
		audio = new DesktopAudio(path);
		
		fileLogic.create(this);
		ui.create(this);
		audio.create(this);
		
		ui.startUI();
	}

}
