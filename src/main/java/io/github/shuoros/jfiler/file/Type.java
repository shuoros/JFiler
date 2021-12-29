package io.github.shuoros.jfiler.file;

/**
 * Enumeration af files different types.
 *
 * @author Soroush Shemshadi
 * @version 1.0.0
 * @since 1.0.0
 */
public enum Type {

    Folder("Folder"),
    TXT("txt");

    private final String type;

    Type(String type) {
        this.type = type;
    }

    /**
     * Extract {@link io.github.shuoros.jfiler.file.Type} with name of it.
     *
     * @param type A valid file type.
     * @return Enum of given type. null if given type is un valid.
     */
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
