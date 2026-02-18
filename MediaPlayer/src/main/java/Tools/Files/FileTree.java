package Tools.Files;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileTree {

    private final Path root;
    private final Map<Path, FileManager> files;

    public FileTree(Path root) throws IOException {
        this.root = root;
        this.files = new HashMap<>();
        Files.createDirectories(root);
        scan();
    }

    private void scan() throws IOException {
        Files.walk(root)
             .filter(Files::isRegularFile)
             .forEach(p -> {
				try {
					Path rel = root.relativize(p);
					if(!files.containsKey(rel))
						files.put(rel, new FileManager(p));
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
    }
    public FileManager createFile(Path relativePath) throws IOException {
        Path path = root.resolve(relativePath);
        Path parent = path.getParent();
        if(parent != null)
        	Files.createDirectories(parent);
        FileManager fm = new FileManager(path);
        files.put(relativePath, fm);
        return fm;
    }
    public FileManager rename(Path oldRelative, Path newRelative) throws IOException {
    	Path oldAbs = root.resolve(oldRelative);
    	Path newAbs = root.resolve(newRelative);
    	if(Files.notExists(oldAbs)) return null;
    	
    	Files.createDirectories(newAbs.getParent());
    	Files.move(oldAbs, newAbs);
    	
    	files.remove(oldRelative);
    	
    	FileManager fm = new FileManager(newAbs);
    	files.put(newRelative, fm);
    	
        return fm;
    }
    public void remove(Path relativePath) throws IOException {
        Path path = root.resolve(relativePath);
    	Files.delete(path);
    	files.remove(relativePath);
    }
    public Path getNextFreeFileName(Path relativePath) {
        Path path = root.resolve(relativePath);
        if(Files.notExists(path)) {
        	return root.relativize(path);
        }
        String fileName = path.getFileName().toString();
        String[] nameAndType = Util.getNameAndType(fileName);
        String name = nameAndType[0], type = nameAndType[1];
   
        int i = 1;
        while(true) {
        	Path newPath = path.getParent().resolve(name + " (" + (i++) + ")" + type);
        	if(Files.notExists(newPath)) return root.relativize(newPath);
        }
    }
    public Path createFolder(Path relativePath) throws IOException {
        Path folder = root.resolve(relativePath);
        return Files.createDirectories(folder);
    }
    public FileManager get(Path relativePath) {
        return files.get(relativePath);
    }
    public FileManager getOrCreate(Path relativePath) {
    	return getOrCreateManager(root.resolve(relativePath));
    }
    
    public List<FileManager> getAll(Path relativePath) throws IOException {
    	return Files.walk(root.resolve(relativePath)).
    			filter(Files::isRegularFile).map(this::getOrCreateManager).toList();
    }
    private FileManager getOrCreateManager(Path absPath){
    	Path relativePath = root.relativize(absPath);
    	if(files.containsKey(relativePath)) return files.get(relativePath);
    	try {
    		Path parent = absPath.getParent();
            if(parent != null)
            	Files.createDirectories(parent);
			return files.put(relativePath, new FileManager(absPath));
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return null;
    }
    public Map<Path, FileManager> get() {
        return files;
    }
    public Path getPath() {
    	return root;
    }
}
