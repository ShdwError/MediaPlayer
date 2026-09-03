package org.mediaplayer.server;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import org.mediaplayer.core.Generics.GenericFileLogic;

import Tools.Core.Files.FileTree;
import Tools.Core.Files.GenericFileTree;
import Tools.Core.Files.Data.DataTypes.DataString;
import Tools.Core.Files.Data.Exceptions.DataTypeException;

public class ServerFileLogic extends GenericFileLogic {
	private MediaPlayerServer server;

	public ServerFileLogic(Path path) throws IOException {
		super(path);
	}
	
	public void create(MediaPlayerServer server) throws IOException, DataTypeException {
		this.server = server;
		this.audio = new AudioMock();
		create();
	}

	@Override
	public GenericFileTree createFileTree() throws IOException {
		return new FileTree(path);
	}

}
