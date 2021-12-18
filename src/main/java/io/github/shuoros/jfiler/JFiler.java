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

    private File clipBoard;
    private boolean copy;
    private boolean cut;
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
        this.copy = false;
        this.cut = false;
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

    public void copyTo(String source, String destination) throws IOException {
        createNewFile(destination);
        InputStream is = new FileInputStream(source);
        OutputStream os = new FileOutputStream(destination);
        writeFromInputStreamToOutputStream(is, os);
    }

    public void copy(String source) {
        this.clipBoard = new File(Paths.get(source));
        this.copy = true;
        this.cut = false;
    }

    public void cutTo(String source, String destination) throws IOException {
        copyTo(source, destination);
        delete(source);
    }

    public void cut(File source) {
        this.clipBoard = source;
        this.cut = true;
        this.copy = false;
    }

    public void paste(String destination) throws IOException {
        if(this.clipBoard == null)
            if(this.copy)
                copyTo(this.clipBoard.getPath(), destination);
            else if(this.cut)
                cutTo(this.clipBoard.getPath(), destination);

        this.clipBoard = null;
        this.copy = false;
        this.cut = false;
    }

    public void delete(String destination) throws IOException {
        java.io.File file = new java.io.File(destination);
        if (!file.delete())
            throw new IOException(//
                    "Failed to delete the file because: " +//
                            getReasonForFileDeletionFailureInPlainEnglish(file));
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

    private void writeFromInputStreamToOutputStream(InputStream is, OutputStream os) throws IOException {
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) > 0) {
            os.write(buffer, 0, length);
        }

        is.close();
        os.close();
    }

    private String getReasonForFileDeletionFailureInPlainEnglish(java.io.File file) {
        try {
            if (!file.exists())
                return "It doesn't exist in the first place.";
            else if (file.isDirectory() && Objects.requireNonNull(file.list()).length > 0)
                return "It's a directory and it's not empty.";
            else
                return "Somebody else has it open, we don't have write permissions, or somebody stole my disk.";
        } catch (SecurityException e) {
            return "We're sandboxed and don't have filesystem access.";
        }
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
