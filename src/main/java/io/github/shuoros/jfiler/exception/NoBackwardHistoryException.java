package io.github.shuoros.jfiler.exception;

/**
 * Signals that a Runtime exception in go backward from current location of {@link io.github.shuoros.jfiler.JFiler}
 * has occurred.
 *
 * @author Soroush Shemshadi
 * @version 1.0.0
 * @see java.lang.RuntimeException
 * @since 1.0.0
 */
public class NoBackwardHistoryException extends RuntimeException {

    public NoBackwardHistoryException() {
        super("You can't go backward, There is no history of rear folders!");
    }

}
