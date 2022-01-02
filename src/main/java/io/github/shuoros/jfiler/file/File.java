package io.github.shuoros.jfiler.file;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;

/**
 * A model that inherits {@link java.io.File} and represents a file.
 *
 * @author Soroush Shemshadi
 * @version 1.0.0
 * @see java.io.File
 * @since 1.0.0
 */
public class File extends java.io.File {

    private final Path location;
    private Type type;
    private Date createdDate;
    private Date lastModifiedDate;

    /**
     * Constructs a {@link io.github.shuoros.jfiler.file.File} instance in your given path.
     *
     * @param location Location of your desired file.
     */
    public File(Path location) {
        super(location.toString());
        if (exists())
            extractFileAttributes(location);
        this.location = location;
        this.type = super.isFile() ? Type.type(super.getName().substring(super.getName().lastIndexOf('.') + 1)) : Type.Folder;
    }

    /**
     * Creates a new instance of {@link io.github.shuoros.jfiler.file.File} for you.
     *
     * @param location Location of your desired file.
     * @return A new instance of File.
     */
    public static File open(Path location) {
        return new File(location);
    }

    /**
     * Creates a new file for you and returns a {@link io.github.shuoros.jfiler.file.File} instance
     * which hold your new file.
     *
     * @param location Location which you want to create a new folder in it.
     * @return An instance of Folder which hold your newly created folder.
     * @throws IOException If anything goes wrong in creating a new folder an IOException will be thrown.
     */
    public static File create(Path location) throws IOException {
        File file = null;
        if (new java.io.File(location.toString()).createNewFile())
            file = new File(location);
        else
            throw new FileAlreadyExistsException(location.toString());
        return file;
    }

    public static Boolean exist(Path location){
        return location.toFile().exists();
    }

    /**
     * Gets type of file.
     *
     * @return Type of file.
     */
    public Type getType() {
        return type;
    }

    /**
     * Gets location of file.
     *
     * @return Location of file.
     */
    public Path getLocation() {
        return location;
    }

    /**
     * Gets parent folder of file.
     *
     * @return Parent folder of file.
     */
    public Folder getParentFolder() {
        return new Folder(this.location.getParent());
    }

    /**
     * Gets size of file.
     *
     * @return Size of file.
     */
    public Long getSize() {
        return super.length();
    }

    /**
     * Gets creation date of file.
     *
     * @return Creation date of file.
     */
    public Date getCreatedDate() {
        return createdDate;
    }

    /**
     * Gets last modification date of file.
     *
     * @return Last modification date of file.
     */
    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    /**
     * Compares two files by their location.
     *
     * @param o Other file to compare.
     * @return True if location of other file equals with location of this file and false if not.
     */
    @Override
    public boolean equals(Object o) {
        return this.location.equals(((File) o).getLocation());
    }

    @Override
    public String toString() {
        return "File{" +
                "name='" + super.getName() + '\'' +
                ", type=" + type +
                ", location=" + location +
                ", size=" + super.length() + " bytes " +
                ", created=" + createdDate +
                ", lastModified=" + lastModifiedDate +
                '}';
    }

    private void extractFileAttributes(Path location) {
        BasicFileAttributes attr = getBasicFileAttributesClass(location);
        this.createdDate = new Date(attr.creationTime().toMillis());
        this.lastModifiedDate = new Date(attr.lastModifiedTime().toMillis());
    }

    private BasicFileAttributes getBasicFileAttributesClass(Path location) {
        BasicFileAttributes attr = null;
        try {
            attr = Files.readAttributes(location, BasicFileAttributes.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return attr;
    }

}
