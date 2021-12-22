package io.github.shuoros.jfiler;

import io.github.shuoros.jfiler.file.File;
import io.github.shuoros.jfiler.file.Folder;
import io.github.shuoros.jfiler.util.SystemOS;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Stack;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class JFiler {

    private final Boolean lock;
    private final Folder homeLocation;
    private final Stack<Folder> frontLocation = new Stack<>();
    private final Stack<Folder> rearLocation = new Stack<>();
    private boolean copy;
    private boolean cut;
    private File clipBoard;
    private Folder currentLocation;

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

    public static File getFile(String location) {
        return new File(Paths.get(location));
    }

    public static Folder getFolder(String location) {
        return new Folder(Paths.get(location));
    }

    public Folder getCurrentLocation() {
        return currentLocation;
    }

    public List<File> getList() {
        return currentLocation.getContains();
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

    public void hide(String destination) throws IOException {
        if (new java.io.File(destination).isHidden())
            throw new FileIsAlreadyHideException(destination);

        if (SystemOS.isUnix() || SystemOS.isMac())
            hideFileInUnix(destination);
        else if (SystemOS.isWindows())
            hideFileInWindows(destination);
    }

    public void unHide(String destination) throws IOException {
        if (!new java.io.File(destination).isHidden())
            throw new FileIsAlreadyUnHideException(destination);

        if (SystemOS.isUnix() || SystemOS.isMac())
            unHideFileInUnix(destination);
        else if (SystemOS.isWindows())
            unHideFileInWindows(destination);
    }

    public void rename(String destination, String newName) throws IOException {
        cutTo(destination, newDestination(destination, newName));
    }

    public void cutTo(String source, String destination) throws IOException {
        copyTo(source, destination);
        delete(source);
    }

    public void cut(String source) {
        this.clipBoard = new File(Paths.get(source));
        this.cut = true;
        this.copy = false;
    }

    public void copyTo(String source, String destination) throws IOException {
        if (new java.io.File(source).isFile())
            copyFile(source, destination);
        else
            copyFolder(source, destination);
    }

    public void copy(String source) {
        this.clipBoard = new File(Paths.get(source));
        this.copy = true;
        this.cut = false;
    }

    public void paste(String destination) throws IOException {
        if (this.clipBoard == null)
            if (this.copy)
                copyTo(this.clipBoard.getPath(), destination);
            else if (this.cut)
                cutTo(this.clipBoard.getPath(), destination);

        this.clipBoard = null;
        this.copy = false;
        this.cut = false;
    }

    public void zip(List<String> destinations, String zipFileDestination) throws IOException {
        if (zipFileDestination.endsWith(".zip"))
            zipFileDestination = zipFileDestination.replaceAll(".zip$", "");

        createNewFolder(zipFileDestination);
        for (String destination : destinations)
            copyTo(destination,//
                     zipFileDestination + "/" + destination.split("/")[destination.split("/").length -1]);

        compress(zipFileDestination);
        delete(zipFileDestination);
    }

    public void delete(String destination) throws IOException {
        java.io.File file = new java.io.File(destination);
        if (file.isFile()) {
            if (!file.delete())
                throw new IOException(//
                        "Failed to delete the file because: " +//
                                getReasonForFileDeletionFailureInPlainEnglish(file));
        } else
            deleteFolder(destination);
    }

    public void createNewFile(String destination) throws IOException {
        if (isFileExist(destination))
            throw new FileAlreadyExistsException(destination);

        File.create(Paths.get(destination));
    }

    public void createNewFolder(String destination) throws IOException {
        if (isFileExist(destination))
            throw new FileAlreadyExistsException(destination);

        Folder.create(Paths.get(destination));
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

    private String newDestination(String destination, String newName) {
        destination = destination.replaceAll("\\\\", "/");
        StringBuilder newDestination = new StringBuilder();
        for (String directory : destination.split("/"))
            if (directory.equals(destination.split("/")[destination.split("/").length - 1]))
                newDestination.append(newName);
            else
                newDestination.append(directory).append("/");

        return newDestination.toString();
    }

    private void hideFileInUnix(String destination) throws IOException {
        rename(destination, "." + destination.split("/")[destination.split("/").length - 1]);
    }

    private void hideFileInWindows(String destination) throws IOException {
        Files.setAttribute(Paths.get(destination), "dos:hidden", true, LinkOption.NOFOLLOW_LINKS);
    }

    private void unHideFileInUnix(String destination) throws IOException {
        rename(destination, destination.split("/")[destination.split("/").length - 1]//
                .replaceFirst(".", ""));
    }

    private void unHideFileInWindows(String destination) throws IOException {
        Files.setAttribute(Paths.get(destination), "dos:hidden", false, LinkOption.NOFOLLOW_LINKS);
    }

    private void compress(String destination) {
        final Path sourceDir = Paths.get(destination);
        String zipFileName = destination.concat(".zip");
        try {
            final ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(zipFileName));
            Files.walkFileTree(sourceDir, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) {
                    try {
                        Path targetFile = sourceDir.relativize(file);
                        outputStream.putNextEntry(new ZipEntry(targetFile.toString()));
                        byte[] bytes = Files.readAllBytes(file);
                        outputStream.write(bytes, 0, bytes.length);
                        outputStream.closeEntry();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void copyFolder(String source, String destination) throws IOException {
        createNewFolder(destination);
        for (String file : Objects.requireNonNull(new java.io.File(source).list())) {
            if (new java.io.File(source + "/" + file).isFile())
                copyFile(source + "/" + file, destination + "/" + file);
            else
                copyFolder(source + "/" + file, destination + "/" + file);
        }
    }

    private void copyFile(String source, String destination) throws IOException {
        createNewFile(destination);
        InputStream is = new FileInputStream(source);
        OutputStream os = new FileOutputStream(destination);
        writeFromInputStreamToOutputStream(is, os);
    }

    private void deleteFolder(String destination) throws IOException {
        Files.walk(Paths.get(destination))
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(java.io.File::delete);
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

    private static class FileIsAlreadyHideException extends RuntimeException {

        public FileIsAlreadyHideException(String destination) {
            super(destination);
        }

    }

    private static class FileIsAlreadyUnHideException extends RuntimeException {

        public FileIsAlreadyUnHideException(String destination) {
            super(destination);
        }

    }

}
