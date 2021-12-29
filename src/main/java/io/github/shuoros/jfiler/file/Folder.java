package io.github.shuoros.jfiler.file;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * A model that inherits {@link io.github.shuoros.jfiler.file.File} and represents a folder
 * (which is itself a type of file). This class, in addition to the information in the file class,
 * holds the folder size and a list of all the files it contains.
 *
 * @author Soroush Shemshadi
 * @version 1.0.0
 * @see io.github.shuoros.jfiler.file.File
 * @since 1.0.0
 */
public class Folder extends File {

    private final List<File> contains = new ArrayList<>();
    private final Long size;

    /**
     * Constructs a {@link io.github.shuoros.jfiler.file.Folder} instance in your given path.
     *
     * @param location Location of your desired folder.
     */
    public Folder(Path location) {
        super(location);
        if (super.exists())
            extractContainedFilesAndFolders(location);
        size = calculateFolderSize(this);
    }

    /**
     * Creates a new instance of {@link io.github.shuoros.jfiler.file.Folder} for you.
     *
     * @param location Location of your desired folder.
     * @return A new instance of Folder.
     */
    public static Folder open(Path location) {
        return new Folder(location);
    }

    /**
     * Creates a new folder for you and returns a {@link io.github.shuoros.jfiler.file.Folder} instance
     * which hold your new folder.
     *
     * @param location Location which you want to create a new folder in it.
     * @return An instance of Folder which hold your newly created folder.
     * @throws IOException If anything goes wrong in creating a new folder an IOException will be thrown.
     */
    public static Folder create(Path location) throws IOException {
        Folder folder = null;
        if (new java.io.File(location.toString()).mkdir())
            folder = new Folder(location);
        else
            throw new FileAlreadyExistsException(location.toString());
        return folder;
    }

    /**
     * Gets list of sub files and folders in this folder.
     *
     * @return A list of sub files and folders in this folder.
     */
    public List<File> getContains() {
        return contains;
    }

    /**
     * Size of folder in bytes.
     *
     * @return Size of folder in bytes.
     */
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
