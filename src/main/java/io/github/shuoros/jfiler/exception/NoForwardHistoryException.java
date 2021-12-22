package io.github.shuoros.jfiler.exception;

public class NoForwardHistoryException extends RuntimeException {

    public NoForwardHistoryException() {
        super("You can't go forward, There is no history of front folders!");
    }

}
