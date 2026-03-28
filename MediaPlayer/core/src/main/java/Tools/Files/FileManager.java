package Tools.Files;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class FileManager {

    private final Path path;

    public FileManager(Path path) throws IOException {
        Files.createDirectories(path.getParent());
        this.path = path;

        if (Files.notExists(path)) {
            Files.createFile(path);
        }
    }

    public List<String> read() throws IOException {
        return Files.readAllLines(path);
    }

    public void write(List<String> lines) throws IOException {
        Files.write(path, lines);
    }

    public void write(String text) throws IOException {
        Files.writeString(path, text);
    }

    public void append(String text) throws IOException {
        Files.writeString(path, text, StandardOpenOption.APPEND);
    }

    public Path getPath() {
        return path;
    }
}
