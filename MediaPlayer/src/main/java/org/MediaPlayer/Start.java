package org.MediaPlayer;

import java.nio.file.Path;

import javafx.application.Application;
import javafx.stage.Stage;

public class Start extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		App app = new App(Path.of("Test").toAbsolutePath());
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}
