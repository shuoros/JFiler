package io.github.shuoros.jfiler.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;

public class File {

    private String name;
    private Type type;
    private Path location;
    private Long size;
    private Date created;
    private Date lastModified;

    public File(Path location) throws IOException {
        java.io.File file = location.toFile();
        BasicFileAttributes attr = Files.readAttributes(location, BasicFileAttributes.class);
        this.name = file.getName();
        if (file.isFile()) {
            this.type = Type.type(file.getName().substring(file.getName().lastIndexOf('.') + 1));
        }
        this.location = location;
        this.size = file.length();
        this.created = new Date(attr.creationTime().toMillis());
        this.lastModified = new Date(attr.lastModifiedTime().toMillis());
    }

    public String getName() {
        return name;
    }

    public File setName(String name) {
        this.name = name;
        return this;
    }

    public Type getType() {
        return type;
    }

    public File setType(Type type) {
        this.type = type;
        return this;
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
                "name='" + name + '\'' +
                ", type=" + type +
                ", location=" + location +
                ", size=" + size +
                ", created=" + created +
                ", lastModified=" + lastModified +
                '}';
    }

}
