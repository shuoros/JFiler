package io.github.shuoros.jfiler.exception;

public class CannotSearchInFileException extends RuntimeException {

    public CannotSearchInFileException(String destination) {
        super(destination);
    }

}
