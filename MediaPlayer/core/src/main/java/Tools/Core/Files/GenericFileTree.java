package Tools.Core.Files;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public abstract class GenericFileTree {
    public abstract GenericFileManager createFile(Path relativePath) throws IOException;
    public abstract GenericFileManager rename(Path oldRelative, Path newRelative) throws IOException;
    public abstract void remove(Path relativePath) throws IOException;
    public abstract Path getNextFreeFileName(Path relativePath);
    public abstract void createFolder(Path relativePath) throws IOException;
    public abstract GenericFileManager get(Path relativePath);
    public abstract GenericFileManager getOrCreate(Path relativePath);
    public abstract List<GenericFileManager> getAll(Path relativePath) throws IOException;
    public abstract Map<Path, GenericFileManager> get();
    public abstract Path getPath();
}
