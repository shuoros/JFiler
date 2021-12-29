package io.github.shuoros.jfiler.exception;

/**
 * Signals that a Runtime exception in go forward from current location of {@link io.github.shuoros.jfiler.JFiler}
 * has occurred.
 *
 * @author Soroush Shemshadi
 * @version 1.0.0
 * @see java.lang.RuntimeException
 * @since 1.0.0
 */
public class NoForwardHistoryException extends RuntimeException {

    public NoForwardHistoryException() {
        super("You can't go forward, There is no history of front folders!");
    }

}
