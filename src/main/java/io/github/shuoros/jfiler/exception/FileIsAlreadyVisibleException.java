package io.github.shuoros.jfiler.exception;

public class FileIsAlreadyVisibleException extends RuntimeException {

    public FileIsAlreadyVisibleException(String destination) {
        super(destination);
    }

}
