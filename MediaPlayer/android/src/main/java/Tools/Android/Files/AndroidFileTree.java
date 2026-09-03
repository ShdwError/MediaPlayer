package Tools.Files;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AndroidFileTree extends GenericFileTree {
    private final File root;
    private final Map<Path, GenericFileManager> files;

    public AndroidFileTree(Context context, Path rootFolder) throws IOException {
        this.root = new File(context.getExternalFilesDir(null), rootFolder.toString());
        this.files = new HashMap<>();

        if(!root.exists())
            root.mkdirs();

        scan(root);
    }

    private void scan(File dir) throws IOException {
        File[] list = dir.listFiles();
        if(list == null) return;

        for(File f : list) {
            if(f.isDirectory()) {
                scan(f);
            }
            else {
                Path relativePath = getRelativePath(f);
                if(!files.containsKey(relativePath)) {
                    files.put(relativePath, new AndroidFileManager(f));
                }
            }
        }
    }
    @Override
    public AndroidFileManager createFile(Path relativePath) throws IOException {
        File file = new File(root, relativePath.toString());
        File parent = file.getParentFile();
        if(parent != null && !parent.exists()) parent.mkdirs();

        file.createNewFile();

        AndroidFileManager fm = new AndroidFileManager(file);
        files.put(relativePath, fm);
        return fm;
    }
    @Override
    public AndroidFileManager rename(Path oldRelative, Path newRelative) throws IOException {
        File oldFile = new File(root, oldRelative.toString());
        File newFile = new File(root, newRelative.toString());
        if(!oldFile.exists()) return null;

        File parent = newFile.getParentFile();
        if(parent != null) parent.mkdirs();

        oldFile.renameTo(newFile);

        files.remove(oldRelative);

        AndroidFileManager fm = new AndroidFileManager(newFile);
        files.put(newRelative, fm);

        return fm;
    }
    @Override
    public void remove(Path relativePath) throws IOException {
        File file = new File(root, relativePath.toString());
        file.delete();
        files.remove(relativePath);
    }
    @Override
    public Path getNextFreeFileName(Path relativePath) {
        File file = new File(root, relativePath.toString());
        if(!file.exists()) {
            return relativePath;
        }
        String fileName = file.getName();
        String[] nameAndType = Util.getNameAndType(fileName);
        String name = nameAndType[0], type = nameAndType[1];

        File parent = file.getParentFile();

        int i = 1;
        while(true) {
            String newPath = name + " (" + (i++) + ")" + type;
            File newFile = new File(root, newPath);
            if(!newFile.exists())
                return getRelativePath(newFile);
        }
    }
    @Override
    public void createFolder(Path relativePath) throws IOException {
        File parent = new File(root, relativePath.toString());
        parent.mkdirs();
    }
    @Override
    public AndroidFileManager get(Path relativePath) {
        return (AndroidFileManager) files.get(relativePath);
    }
    @Override
    public AndroidFileManager getOrCreate(Path relativePath) {
        AndroidFileManager fm = (AndroidFileManager) files.get(relativePath);
        if(fm == null) {
            try {
                fm = createFile(relativePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return fm;
    }
    @Override
    public List<GenericFileManager> getAll(Path relatvePath) throws IOException {
        return crawlAll(new File(root, relatvePath.toString()));
    }
    private List<GenericFileManager> crawlAll(File dir) throws IOException {
        List<GenericFileManager> ret = new ArrayList<>();
        File[] list = dir.listFiles();
        if(list == null) return ret;

        for(File f : list) {
            if (f.isDirectory()) {
                ret.addAll(crawlAll(f));
            }
            else {
                Path relativePath = getRelativePath(f);
                GenericFileManager fm = files.get(relativePath);
                if(fm == null) {
                    fm = files.put(relativePath, new AndroidFileManager(f));
                }
                ret.add(fm);
            }
        }
        return ret;
    }
    @Override
    public Map<Path, GenericFileManager> get() {
        return files;
    }
    @Override
    public Path getPath() {
        return root.toPath();
    }
    private Path getRelativePath(File file) {
        Path rootPath = root.toPath();
        Path filePath = file.toPath();

        return rootPath.relativize(filePath);
    }
}
