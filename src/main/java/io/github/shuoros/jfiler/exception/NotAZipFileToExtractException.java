package io.github.shuoros.jfiler.exception;

public class NotAZipFileToExtractException extends RuntimeException {

    public NotAZipFileToExtractException(String destination) {
        super(destination);
    }

}
