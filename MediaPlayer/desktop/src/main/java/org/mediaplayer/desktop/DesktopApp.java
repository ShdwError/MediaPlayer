package org.mediaplayer.desktop;

import org.mediaplayer.core.Generics.GenericFileLogic;

import java.io.IOException;
import java.nio.file.Path;

public class DesktopApp {
	public ConsoleUI ui;
	public DesktopApp(Path path) throws IOException {
		DesktopFileLogic fileLogic = new DesktopFileLogic(path);
		ConsoleUI ui = new ConsoleUI();
		DesktopAudio audio = new DesktopAudio(path);
		
		fileLogic.create(ui, audio);
		ui.create(audio, fileLogic);
		audio.create(fileLogic, ui);
		
		ui.startUI();
	}

}
