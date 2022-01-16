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
public class LocationNotFoundException extends RuntimeException {

    public LocationNotFoundException(String location) {
        super(location);
    }

}
