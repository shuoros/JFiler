package io.github.shuoros.jfiler.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;

public class File extends java.io.File {

    private Type type;
    private Path location;
    private Long size;
    private Date created;
    private Date lastModified;

    public File(Path location) throws IOException {
        super(location.toString());
        BasicFileAttributes attr = Files.readAttributes(location, BasicFileAttributes.class);
        if (super.isFile()) {
            this.type = Type.type(super.getName().substring(super.getName().lastIndexOf('.') + 1));
        }
        this.location = location;
        this.size = super.length();
        this.created = new Date(attr.creationTime().toMillis());
        this.lastModified = new Date(attr.lastModifiedTime().toMillis());
    }

    public String getName() {
        return super.getName();
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

    public File setLocation(Path location) {
        this.location = location;
        return this;
    }

    public Long getSize() {
        return size;
    }

    public File setSize(Long size) {
        this.size = size;
        return this;
    }

    public Date getCreated() {
        return created;
    }

    public File setCreated(Date created) {
        this.created = created;
        return this;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public File setLastModified(Date lastModified) {
        this.lastModified = lastModified;
        return this;
    }

    @Override
    public String toString() {
        return "File{" +
                "name='" + super.getName() + '\'' +
                ", type=" + type +
                ", location=" + location +
                ", size=" + size +
                ", created=" + created +
                ", lastModified=" + lastModified +
                '}';
    }

}
