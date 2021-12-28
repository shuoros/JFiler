package io.github.shuoros.jfiler.exception;

public class NoBackwardHistoryException extends RuntimeException {

    public NoBackwardHistoryException() {
        super("You can't go backward, There is no history of rear folders!");
    }

}
