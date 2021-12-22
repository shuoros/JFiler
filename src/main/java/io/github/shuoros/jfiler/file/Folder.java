package io.github.shuoros.jfiler.file;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Folder extends File {

    private final List<File> contains = new ArrayList<>();
    private final Long size;

    public Folder(Path location) {
        super(location);
        extractContainedFilesAndFolders(location);
        size = calculateFolderSize(this);
    }

    public static Folder open(Path location) {
        return new Folder(location);
    }

    public static Folder create(Path location) throws IOException {
        Folder folder = null;
        if (new java.io.File(location.toString()).mkdir())
            folder = new Folder(location);
        else
            throw new FileAlreadyExistsException(location.toString());
        return folder;
    }

    public List<File> getContains() {
        return contains;
    }

    @Override
    public Long getSize() {
        return size;
    }

    @Override
    public String toString() {
        return "Folder{" +
                "name='" + super.getName() + '\'' +
                ", location=" + super.getLocation() +
                ", size=" + this.size + " bytes " +
                ", created=" + super.getCreatedDate() +
                ", lastModified=" + super.getLastModifiedDate() +
                '}';
    }

    private void extractContainedFilesAndFolders(Path location) {
        Arrays.stream(Objects.requireNonNull(super.list())).forEach(i -> {
            i = location.toString() + '/' + i;
            contains.add(new java.io.File(i).isFile() ? new File(Paths.get(i)) : new Folder(Paths.get(i)));
        });
    }

    private Long calculateFolderSize(Folder folder) {
        return folder.getContains().stream()//
                .mapToLong(file -> file.isFile() ? file.getSize() : calculateFolderSize((Folder) file)).sum();
    }

}
