package io.github.shuoros.jfiler.exception;

/**
 * Signals that a Runtime exception in hiding a hidden file has occurred.
 *
 * @author Soroush Shemshadi
 * @version 1.0.0
 * @see java.lang.RuntimeException
 * @since 1.0.0
 */
public class FileIsAlreadyHideException extends RuntimeException {

    public FileIsAlreadyHideException(String destination) {
        super(destination);
    }

}
