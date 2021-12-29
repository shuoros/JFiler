package io.github.shuoros.jfiler.exception;

/**
 * Signals that a Runtime exception in search for a regex in file has occurred.
 *
 * @author Soroush Shemshadi
 * @version 1.0.0
 * @see java.lang.RuntimeException
 * @since 1.0.0
 */
public class CannotSearchInFileException extends RuntimeException {

    public CannotSearchInFileException(String destination) {
        super(destination);
    }

}
