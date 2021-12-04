package io.github.shuoros.jfiler.file;

public enum Type {

    Folder("Folder"),
    TXT("txt");

    private final String type;

    Type(String type) {
        this.type = type;
    }

    public static Type type(String type) {
        for (Type typ : Type.values()) {
            if (typ.type.equalsIgnoreCase(String.valueOf(type))) {
                return typ;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.type;
    }

}
