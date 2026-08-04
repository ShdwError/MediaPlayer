package org.mediaplayer.desktop;

import org.mediaplayer.core.Generics.GenericFileLogic;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import Tools.Files.Data.DataTypes.DataString;
import Tools.Files.Data.Exceptions.DataTypeException;
import Tools.Files.FileTree;
import Tools.Files.GenericFileTree;

public class DesktopFileLogic extends GenericFileLogic {
	private ConsoleUI ui;

	public DesktopFileLogic(Path path) throws IOException {
		super(path);
	}

	public void create(ConsoleUI ui, DesktopAudio audio) throws IOException, DataTypeException {
		this.ui = ui;
		this.audio = audio;
		super.create();
	}

	@Override
	public GenericFileTree createFileTree() throws IOException {
		return new FileTree(path);
	}

	@Override
	public void correctPathData(Map<String, DataString> map) {

	}
}
