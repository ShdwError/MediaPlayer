package Tools.Files;


import android.content.Context;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class AndroidFileManager extends GenericFileManager {

    private final File file;
    public AndroidFileManager(File file) throws IOException {
        this.file = file;

        if(!file.exists())
            file.createNewFile();
    }

    @Override
    public List<String> read() throws IOException {
        List<String> lines = new ArrayList<>();

        try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            while(line != null) {
                lines.add(line);
                line = reader.readLine();
            }
        }

        return lines;
    }
    @Override
    public void write(List<String> lines) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }
    @Override
    public void write(String text) throws IOException {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
            writer.write(text);
        }
    }
    @Override
    public void append(String text) throws IOException {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(text);
        }
    }

    @Override
    public Path getPath() {
        return Path.of(file.toString());
    }
}
