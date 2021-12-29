package io.github.shuoros.jfiler.exception;

/**
 * Signals that a Runtime exception in trying to extract a non zip file has occurred.
 *
 * @author Soroush Shemshadi
 * @version 1.0.0
 * @see java.lang.RuntimeException
 * @since 1.0.0
 */
public class NotAZipFileToExtractException extends RuntimeException {

    public NotAZipFileToExtractException(String destination) {
        super(destination);
    }

}
