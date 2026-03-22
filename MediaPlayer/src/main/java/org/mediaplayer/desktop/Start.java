package org.mediaplayer.desktop;

import java.nio.file.Path;

import org.mediaplayer.core.App;

import javafx.application.Application;
import javafx.stage.Stage;

public class Start extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		App app = new App(Path.of("MediaPlayer").toAbsolutePath());
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}
