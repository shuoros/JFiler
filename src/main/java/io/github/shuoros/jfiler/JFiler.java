package io.github.shuoros.jfiler;

import io.github.shuoros.jfiler.file.File;
import io.github.shuoros.jfiler.file.Folder;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

public class JFiler {

    private Folder currentLocation;
    private final Boolean lock;
    private final Folder homeLocation;
    private final Stack<Folder> frontLocation = new Stack<>();
    private final Stack<Folder> rearLocation = new Stack<>();

    public JFiler() {
        this("/", false);
    }

    public JFiler(String location) {
        this(location, false);
    }

    public JFiler(String location, Boolean lock) {
        this.homeLocation = new Folder(Paths.get(location));
        this.currentLocation = this.homeLocation;
        this.lock = lock;
    }

    public static JFiler open(String location) {
        return new JFiler(location);
    }

    public static JFiler openInLockedHome(String location) {
        return new JFiler(location, true);
    }

    public Folder getCurrentLocation() {
        return currentLocation;
    }

    public List<File> getList() {
        return currentLocation.getContains();
    }

    public static File getFile(String location) {
        return new File(Paths.get(location));
    }

    public static Folder getFolder(String location) {
        return new Folder(Paths.get(location));
    }

    public void openFolder(String location) {
        if (this.lock && canNotItGoBackToThisFolder(new Folder(Paths.get(location))))
            throw new HomeIsLockedException();
        this.frontLocation.clear();
        this.rearLocation.push(this.currentLocation);
        this.currentLocation = new Folder(Paths.get(location));
    }

    public void goBackward() {
        if (this.rearLocation.isEmpty())
            throw new NoBackwardHistoryException();
        if (this.lock && canNotItGoBackToThisFolder(this.rearLocation.peek()))
            throw new HomeIsLockedException();
        this.frontLocation.push(this.currentLocation);
        this.currentLocation = this.rearLocation.pop();
    }

    public void goForward() {
        if (this.frontLocation.isEmpty())
            throw new NoForwardHistoryException();
        this.rearLocation.push(this.currentLocation);
        this.currentLocation = this.frontLocation.pop();
    }

    public void goUp() {
        if (this.lock && canNotItGoBackFromThisFolder(this.currentLocation))
            throw new HomeIsLockedException();
        this.frontLocation.push(this.currentLocation);
        this.currentLocation = this.currentLocation.getParentFolder();
    }

    public void createNewFile(String destination) throws IOException {
        if (isFileExist(destination))
            throw new FileAlreadyExistsException(destination);

        File.create(Paths.get(destination));
    }

    public boolean isFileExist(String destination) {
        return new java.io.File(destination).exists();
    }

    private boolean canNotItGoBackFromThisFolder(Folder location) {
        return location.equals(this.homeLocation);
    }

    private boolean canNotItGoBackToThisFolder(Folder location) {
        return !location.getLocation().startsWith(this.homeLocation.getLocation());
    }

    private static class HomeIsLockedException extends RuntimeException {

        public HomeIsLockedException() {
            super("You can't go back any further from this location, Because the home is locked!");
        }

    }

    private static class NoForwardHistoryException extends RuntimeException {

        public NoForwardHistoryException() {
            super("You can't go forward, There is no history of front folders!");
        }

    }

    private static class NoBackwardHistoryException extends RuntimeException {

        public NoBackwardHistoryException() {
            super("You can't go backward, There is no history of rear folders!");
        }

    }

}
