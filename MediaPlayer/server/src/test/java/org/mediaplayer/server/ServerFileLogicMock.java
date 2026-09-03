package org.mediaplayer.server;

import java.io.IOException;
import java.nio.file.Path;

import org.mediaplayer.core.Generics.GenericFileLogic;
import org.mediaplayer.server.ServerFileLogic;

import Tools.Core.Files.GenericFileTree;

public class ServerFileLogicMock extends ServerFileLogic {

	public ServerFileLogicMock() throws IOException {
		super(Path.of(""));
	}

	@Override
	public GenericFileTree createFileTree() throws IOException {
		return new FileTreeMock();
	}

}
