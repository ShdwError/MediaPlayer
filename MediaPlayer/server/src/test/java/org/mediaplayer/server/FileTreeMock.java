package org.mediaplayer.server;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Tools.Core.Files.GenericFileManager;
import Tools.Core.Files.GenericFileTree;

public class FileTreeMock extends GenericFileTree {

	@Override
	public GenericFileManager createFile(Path relativePath) throws IOException {
		return new FileManagerMock();
	}

	@Override
	public GenericFileManager rename(Path oldRelative, Path newRelative) throws IOException {
		return new FileManagerMock();
	}

	@Override
	public void remove(Path relativePath) throws IOException {
		
	}

	@Override
	public Path getNextFreeFileName(Path relativePath) {
		return relativePath;
	}

	@Override
	public void createFolder(Path relativePath) throws IOException {
		
	}

	@Override
	public GenericFileManager get(Path relativePath) {
		return new FileManagerMock();
	}

	@Override
	public GenericFileManager getOrCreate(Path relativePath) {
		return new FileManagerMock();
	}

	@Override
	public List<GenericFileManager> getAll(Path relativePath) throws IOException {
		return List.of();
	}

	@Override
	public Map<Path, GenericFileManager> get() {
		return new HashMap<>();
	}

	@Override
	public Path getPath() {
		return Path.of("");
	}

}
