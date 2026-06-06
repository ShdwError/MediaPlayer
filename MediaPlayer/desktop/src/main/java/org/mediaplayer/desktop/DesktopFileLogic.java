package org.mediaplayer.desktop;

import org.mediaplayer.core.Data.PlaylistsData;
import org.mediaplayer.core.Data.SessionsData;
import org.mediaplayer.core.Data.SoundtracksData;
import org.mediaplayer.core.DataTypes.DataPlaylistEntry;
import org.mediaplayer.core.Generics.GenericFileLogic;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import Tools.Files.Data.DataContainer;
import Tools.Files.Data.DataTypes.DataArray;
import Tools.Files.Data.DataTypes.DataBoolean;
import Tools.Files.Data.DataTypes.DataDate;
import Tools.Files.Data.DataTypes.DataInt;
import Tools.Files.Data.DataTypes.DataMap;
import Tools.Files.Data.DataTypes.DataString;
import Tools.Files.DataSystem;
import Tools.Files.FileManager;
import Tools.Files.FileTree;
import Tools.Files.GenericFileTree;
import Tools.Files.Util;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class DesktopFileLogic extends GenericFileLogic {


	private ConsoleUI ui;

	public DesktopFileLogic(Path path) throws IOException {
		super(path);
	}

	public void create(ConsoleUI ui, DesktopAudio audio) throws IOException {
		super.create();
		this.ui = ui;
		this.audio = audio;
	}

	@Override
	public GenericFileTree createFileTree() throws IOException {
		return new FileTree(path);
	}

	@Override
	public void correctPathData(Map<String, DataString> map) {

	}
}
