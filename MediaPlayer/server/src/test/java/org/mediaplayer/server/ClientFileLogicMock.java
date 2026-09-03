package org.mediaplayer.server;

import java.io.IOException;
import java.nio.file.Path;

import org.mediaplayer.desktop.DesktopFileLogic;

import Tools.Core.Files.GenericFileTree;

public class ClientFileLogicMock extends DesktopFileLogic {

	public ClientFileLogicMock() throws IOException {
		super(Path.of(""));
	}
	@Override
	public GenericFileTree createFileTree() throws IOException {
		return new FileTreeMock();
	}

}
