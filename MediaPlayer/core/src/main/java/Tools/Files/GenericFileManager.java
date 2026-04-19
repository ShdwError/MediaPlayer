package Tools.Files;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public abstract class GenericFileManager {
    public abstract List<String> read() throws IOException;
    public abstract void write(List<String> lines) throws IOException;
    public abstract void write(String text) throws IOException;
    public abstract void append(String text) throws IOException;
    public abstract Path getPath();
}
