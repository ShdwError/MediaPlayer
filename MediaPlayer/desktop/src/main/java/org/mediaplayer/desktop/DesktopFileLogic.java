package org.mediaplayer.desktop;

import org.mediaplayer.core.Generics.GenericFileLogic;

import Tools.Core.Files.FileTree;
import Tools.Core.Files.GenericFileTree;
import Tools.Core.Files.Data.DataTypes.DataString;
import Tools.Core.Files.Data.Exceptions.DataTypeException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

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
}
