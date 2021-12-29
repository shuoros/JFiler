package io.github.shuoros.jfiler.exception;

/**
 * Signals that a Runtime exception in un hiding a visible file has occurred.
 *
 * @author Soroush Shemshadi
 * @version 1.0.0
 * @see java.lang.RuntimeException
 * @since 1.0.0
 */
public class FileIsAlreadyVisibleException extends RuntimeException {

    public FileIsAlreadyVisibleException(String destination) {
        super(destination);
    }

}
