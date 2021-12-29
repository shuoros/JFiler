package io.github.shuoros.jfiler.exception;

/**
 * Signals that a Runtime exception in trying to reach a folder out of {@link io.github.shuoros.jfiler.JFiler}'s home
 * has occurred.
 *
 * @author Soroush Shemshadi
 * @version 1.0.0
 * @see java.lang.RuntimeException
 * @since 1.0.0
 */
public class HomeIsLockedException extends RuntimeException {

    public HomeIsLockedException() {
        super("You can't go back any further, Because the home is locked!");
    }

}
