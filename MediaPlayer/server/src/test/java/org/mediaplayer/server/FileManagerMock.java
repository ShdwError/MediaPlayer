package org.mediaplayer.server;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import Tools.Core.Files.FileManager;
import Tools.Core.Files.GenericFileManager;

public class FileManagerMock extends GenericFileManager {
	@Override
	public List<String> read() throws IOException {
		return new ArrayList<>();
	}

	@Override
	public void write(List<String> lines) throws IOException {
		
	}

	@Override
	public void write(String text) throws IOException {
		
	}

	@Override
	public void append(String text) throws IOException {
		
	}

	@Override
	public Path getPath() {
		return Path.of("");
	}

}
