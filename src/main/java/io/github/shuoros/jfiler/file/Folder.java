package io.github.shuoros.jfiler.file;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class Folder extends File{

    private List<File> contains;

    public Folder(Path location) throws IOException {
        super(location);
        this.setType(Type.Folder);
    }

    public List<File> getContains() {
        return contains;
    }

    public Folder setContains(List<File> contains) {
        this.contains = contains;
        return this;
    }
}
