package io.github.shuoros.jfiler.file;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;

public class File extends java.io.File {

    private Type type;
    private Path location;
    private Date createdDate;
    private Date lastModifiedDate;

    public File(Path location) {
        super(location.toString());
        extractFileAttributes(location);
        this.location = location;
        this.type = super.isFile() ? Type.type(super.getName().substring(super.getName().lastIndexOf('.') + 1)) : Type.Folder;
    }

    public static File open(Path location) {
        return new File(location);
    }

    public static File create(Path location) throws IOException {
        File file = null;
        if (new java.io.File(location.toString()).createNewFile())
            file = new File(location);
        else
            throw new FileAlreadyExistsException(location.toString());
        return file;
    }


    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Path getLocation() {
        return location;
    }

    public Folder getParentFolder() {
        return new Folder(this.location.getParent());
    }

    public Long getSize() {
        return super.length();
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

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
