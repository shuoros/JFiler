package io.github.shuoros.jfiler;

import io.github.shuoros.jfiler.file.File;
import io.github.shuoros.jfiler.file.Folder;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JFiler {

    private Folder location;

    public JFiler(){
    }

    public JFiler(String location) throws IOException {
        this.location = new Folder(Paths.get(location));
    }

    public File getFile(String location) throws IOException {
        return new File(Paths.get(location));
    }

    public Folder getFolder(String location) throws IOException {
        return new Folder(Paths.get(location));
    }

}
