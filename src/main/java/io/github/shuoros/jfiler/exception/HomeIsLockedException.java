package io.github.shuoros.jfiler.exception;

public class HomeIsLockedException extends RuntimeException {

    public HomeIsLockedException() {
        super("You can't go back any further, Because the home is locked!");
    }

}
