package org.mediaplayer.desktop;

import java.io.IOException;
import java.nio.file.Path;

import org.mediaplayer.core.App;

public class DesktopApp extends App {

	public DesktopApp(Path path) throws IOException {
		super(path);
		this.fileLogic = new DesktopFileLogic(path);
		this.ui = new ConsoleUI();
		this.audio = new DesktopAudio(path);
		
		fileLogic.create(this);
		ui.create(this);
		audio.create(this);
		
		ui.startUI();
	}

}
