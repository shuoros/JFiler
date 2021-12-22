package io.github.shuoros.jfiler.exception;

public class FileIsAlreadyHideException extends RuntimeException {

    public FileIsAlreadyHideException(String destination) {
        super(destination);
    }

}
