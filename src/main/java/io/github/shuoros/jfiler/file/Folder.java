package io.github.shuoros.jfiler.file;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Folder extends File {

    private List<File> contains = new ArrayList<>();

    public Folder(Path location) throws IOException {
        super(location);
        this.setType(Type.Folder);
        Arrays.stream(Objects.requireNonNull(super.list())).forEach(i -> {
            i = location.toString() + '/' + i;
            try {
                contains.add(new java.io.File(i).isFile() ? new File(Paths.get(i)) : new Folder(Paths.get(i)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public List<File> getContains() {
        return contains;
    }

    public Folder setContains(List<File> contains) {
        this.contains = contains;
        return this;
    }
}
