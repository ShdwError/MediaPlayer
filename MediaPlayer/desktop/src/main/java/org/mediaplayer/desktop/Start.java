package org.mediaplayer.desktop;

import java.nio.file.Path;

import javafx.application.Application;
import javafx.stage.Stage;

public class Start extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		DesktopApp app = new DesktopApp(Path.of("MediaPlayer").toAbsolutePath());
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}
