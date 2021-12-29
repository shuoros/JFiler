package io.github.shuoros.jfiler;

import io.github.shuoros.jfiler.exception.*;
import io.github.shuoros.jfiler.file.File;
import io.github.shuoros.jfiler.file.Folder;
import io.github.shuoros.jfiler.util.SystemOS;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * JFiller is a library for managing files in Java which easily and with the least line of code gives you
 * the ability to manage files like moving through folders and directories, reading files and folders information,
 * creating new files or folders, making changes to files and folders such as renaming or hiding them, deleting files
 * and folders, searching for files or folders by regex and compressing files and folders or extracting them from zip files.
 *
 * @author Soroush Shemshadi
 * @version 1.0.0
 * @see <a href="https://github.com/shuoros/JFiler">JFiler</a>
 * @since 1.0.0
 */
public class JFiler {

    private final Boolean lock;
    private final Folder homeLocation;
    private final Stack<Folder> frontLocation = new Stack<>();
    private final Stack<Folder> rearLocation = new Stack<>();
    private boolean copy;
    private boolean cut;
    private File clipBoard;
    private Folder currentLocation;

    /**
     * Creates a JFiler instance on the given location.
     *
     * @param location Home of JFiler instance which going to be created.
     */
    public JFiler(String location) {
        this(location, false);
    }

    /**
     * Constructs a JFiler instance on the given location with lock.
     *
     * @param location Home of JFiler instance which going to be created.
     * @param lock     A locked home means you only have access to subfiles and subfolders of home.
     */
    public JFiler(String location, Boolean lock) {
        this.homeLocation = new Folder(Paths.get(pathSeparatorCorrector(location)));
        this.currentLocation = this.homeLocation;
        this.lock = lock;
        this.copy = false;
        this.cut = false;
    }

    /**
     * Creates a new instance of JFiler for you.
     *
     * @param location Home of JFiler instance which going to be created.
     * @return A new instance of JFiler in your desired location.
     */
    public static JFiler open(String location) {
        return new JFiler(location);
    }

    /**
     * Creates a new instance of JFiler for you with locked home.
     * It means that you only have access to subfiles and subfolders of home.
     *
     * @param location Home of JFiler instance which going to be created.
     * @return A new instance of JFiler in your desired location with locked home.
     */
    public static JFiler openInLockedHome(String location) {
        return new JFiler(location, true);
    }

    /**
     * Creates a new instance of {@link io.github.shuoros.jfiler.file.File} in your desired location.
     *
     * @param location Location of file you want to get its instance.
     * @return New instance of File in your desired location.
     */
    public static File getFile(String location) {
        return new File(Paths.get(location));
    }

    /**
     * Creates a new instance of {@link io.github.shuoros.jfiler.file.Folder} in your desired location.
     *
     * @param location Location of folder you want to get its instance.
     * @return New instance of Folder in your desired location.
     */
    public static Folder getFolder(String location) {
        return new Folder(Paths.get(location));
    }

    /**
     * If home is locked or not.
     *
     * @return true if home locked and false if home not locked.
     */
    public boolean isHomeLocked() {
        return this.lock;
    }

    /**
     * Returns an instance of {@link io.github.shuoros.jfiler.file.Folder} which represent the home.
     *
     * @return An instance of Folder which represent the home.
     */
    public Folder getHomeLocation() {
        return homeLocation;
    }

    /**
     * Returns an instance of {@link io.github.shuoros.jfiler.file.Folder}
     * which represent the current location of JFiler.
     *
     * @return An instance of Folder which represent the current location of JFiler.
     */
    public Folder getCurrentLocation() {
        return currentLocation;
    }

    /**
     * Returns an instance of {@link io.github.shuoros.jfiler.file.Folder}
     * which represent the rear location of JFiler.
     *
     * @return An instance of Folder which represent the rear location of JFiler.
     */
    public Folder getRearLocation() {
        return rearLocation.peek();
    }

    /**
     * Returns an instance of {@link io.github.shuoros.jfiler.file.Folder}
     * which represent the front location of JFiler.
     *
     * @return An instance of Folder which represent the front location of JFiler.
     */
    public Folder getFrontLocation() {
        return frontLocation.peek();
    }

    /**
     * Returns an instance of {@link io.github.shuoros.jfiler.file.File} which represent
     * the current file or folder in clipboard.
     *
     * @return An instance of File which represent the current file or folder in clipboard.
     */
    public File getClipBoard() {
        return clipBoard;
    }

    /**
     * Returns a string that indicates the operation that the track is about to perform.
     * It can be a copy or a cut or nothing.
     *
     * @return A string that indicates the operation that the track is about to perform.
     * It can be a copy or a cut or NaN.
     */
    public String getPasteOperation() {
        if (copy)
            return "copy";
        if (cut)
            return "cut";
        return "NaN";
    }

    /**
     * Returns list of all files and folders in current location of JFiler.
     *
     * @return List of all files and folders in current location of JFiler.
     */
    public List<File> getList() {
        return currentLocation.getContains();
    }

    /**
     * Opens a folder in the given location in JFiler's current location. The current location will be added
     * to rear location and the folder you give to function will be set in current location. If home is locked
     * and the given location is outside of home it throws
     * {@link io.github.shuoros.jfiler.exception.HomeIsLockedException}.
     *
     * @param location Location of desired folder which you want to open.
     */
    public void openFolder(String location) {
        location = pathSeparatorCorrector(location);

        if (this.lock && canNotItGoBackToThisFolder(new Folder(Paths.get(location))))
            throw new HomeIsLockedException();
        this.frontLocation.clear();
        this.rearLocation.push(this.currentLocation);
        this.currentLocation = new Folder(Paths.get(location));
    }

    /**
     * Goes backward to the last rear location of JFiler. The current location will be added to front location and
     * last rear location will be set in current location. If there is no backward history
     * it throws {@link io.github.shuoros.jfiler.exception.NoBackwardHistoryException}.
     */
    public void goBackward() {
        if (this.rearLocation.isEmpty())
            throw new NoBackwardHistoryException();
        this.frontLocation.push(this.currentLocation);
        this.currentLocation = this.rearLocation.pop();
    }


    /**
     * Goes forward to the last front location of JFiler. The current location will be added to rear location and
     * last front location will be set in current location. If there is no forward history
     * it throws {@link io.github.shuoros.jfiler.exception.NoForwardHistoryException}.
     */
    public void goForward() {
        if (this.frontLocation.isEmpty())
            throw new NoForwardHistoryException();
        this.rearLocation.push(this.currentLocation);
        this.currentLocation = this.frontLocation.pop();
    }

    /**
     * Goes up to the parent of current location of JFiler. The current location will be added to front location and
     * parent of current location will be set in current location. If home is locked and parent of current location
     * is outside of home it throws {@link io.github.shuoros.jfiler.exception.HomeIsLockedException}.
     */
    public void goUp() {
        if (this.lock && canNotItGoBackFromThisFolder(this.currentLocation))
            throw new HomeIsLockedException();
        this.frontLocation.push(this.currentLocation);
        this.currentLocation = this.currentLocation.getParentFolder();
    }

    /**
     * Hide the given file or folder. If Your machine is Windows its change the file or folders properties
     * to hide it and if your machine is based on unix or mac it will add a dot "." before your desired file
     * or folder's name.
     *
     * @param destination Location of your desired file or folder to hide.
     * @throws IOException If anything goes wrong in changing the file properties or changing its name
     *                     an IOException will be thrown.
     */
    public void hide(String destination) throws IOException {
        destination = pathSeparatorCorrector(destination);

        if (new java.io.File(destination).isHidden())
            throw new FileIsAlreadyHideException(destination);

        if (SystemOS.isUnix() || SystemOS.isMac())
            hideFileInUnix(destination);
        else if (SystemOS.isWindows())
            hideFileInWindows(destination);
    }

    /**
     * Un hides the given file or folder. If Your machine is Windows its change the file or folders properties
     * to un hide it and if your machine is based on unix or mac it will remove the dot "." before your desired
     * file or folder's name.
     *
     * @param destination Location of your desired file or folder to un hide.
     * @throws IOException If anything goes wrong in changing the file properties or changing its name
     *                     an IOException will be thrown.
     */
    public void unHide(String destination) throws IOException {
        destination = pathSeparatorCorrector(destination);

        if (!new java.io.File(destination).isHidden())
            throw new FileIsAlreadyVisibleException(destination);

        if (SystemOS.isUnix() || SystemOS.isMac())
            unHideFileInUnix(destination);
        else if (SystemOS.isWindows())
            unHideFileInWindows(destination);
    }

    /**
     * Renames your desired file or folder to name you want.
     *
     * @param destination Location of your desired file or folder.
     * @param newName     New name of Your desired file or folder.
     * @throws IOException If anything goes wrong in changing the name of your desired file or folder
     *                     an IOException will be thrown.
     */
    public void rename(String destination, String newName) throws IOException {
        cutTo(destination, newDestination(destination, newName));
    }

    /**
     * Cut your desired file or folder in destination you want.
     *
     * @param source      Location of your desired file or folder.
     * @param destination Location which you want your file to be cut there.
     * @throws IOException If anything goes wrong in cutting your desired file or folder an IOException will be thrown.
     */
    public void cutTo(String source, String destination) throws IOException {
        copyTo(source, destination);
        delete(source);
    }

    /**
     * Saves your desired file or folder in clipboard and set pasting operation to cut method.
     *
     * @param source Location of your desired file or folder you want to cut.
     */
    public void cut(String source) {
        source = pathSeparatorCorrector(source);

        this.clipBoard = new File(Paths.get(source));
        this.cut = true;
        this.copy = false;
    }

    /**
     * Copy your desired file or folder in destination you want.
     *
     * @param source      Location of your desired file or folder.
     * @param destination Location which you want your file to be copy there.
     * @throws IOException If anything goes wrong in coping your desired file or folder an IOException will be thrown.
     */
    public void copyTo(String source, String destination) throws IOException {
        source = pathSeparatorCorrector(source);
        destination = pathSeparatorCorrector(destination);

        if (new java.io.File(source).isFile())
            copyFile(source, destination);
        else
            copyFolder(source, destination);
    }

    /**
     * Saves your desired file or folder in clipboard and set pasting operation to copy method.
     *
     * @param source Location of your desired file or folder you want to copy.
     */
    public void copy(String source) {
        source = pathSeparatorCorrector(source);

        this.clipBoard = new File(Paths.get(source));
        this.copy = true;
        this.cut = false;
    }

    /**
     * Pastes the file or folder in clipboard in your desired destination with pasting operation
     * you choose before "copy/cut".
     *
     * @param destination Location which you want to copy or cut file or folder in clipboard.
     * @throws IOException If anything goes wrong in coping or cutting an IOException will be thrown.
     */
    public void paste(String destination) throws IOException {
        destination = pathSeparatorCorrector(destination);

        if (this.clipBoard != null)
            if (this.copy)
                copyTo(this.clipBoard.getPath(), destination);
            else if (this.cut)
                cutTo(this.clipBoard.getPath(), destination);

        this.clipBoard = null;
        this.copy = false;
        this.cut = false;
    }

    /**
     * Compresses desired list of your files or folder into a zip file.
     *
     * @param destinations       List of locations of your files or folder which you want to compress.
     * @param zipFileDestination Location of zip file to save.
     * @throws IOException If anything goes wrong in zipping your files or folders an IOException will be thrown.
     */
    public void zip(List<String> destinations, String zipFileDestination) throws IOException {
        zipFileDestination = pathSeparatorCorrector(zipFileDestination);

        if (zipFileDestination.endsWith(".zip"))
            zipFileDestination = zipFileDestination.replaceAll(".zip$", "");

        createNewFolder(zipFileDestination);
        for (String destination : destinations) {
            destination = pathSeparatorCorrector(destination);
            copyTo(destination,//
                    zipFileDestination + "/" + destination.split("/")[destination.split("/").length - 1]);
        }

        compress(zipFileDestination);
        delete(zipFileDestination);
    }

    /**
     * Unzips your desired zip file in destination you want.
     *
     * @param source      Location of Your zip file.
     * @param destination Location of extracted files or folders from zip file to save.
     * @throws IOException If anything goes wrong in unzipping your files or folders an IOException will be thrown.
     */
    public void unzip(String source, String destination) throws IOException {
        source = pathSeparatorCorrector(source);
        destination = pathSeparatorCorrector(destination);

        if (!source.endsWith(".zip"))
            throw new NotAZipFileToExtractException(source);

        if (!new java.io.File(destination).exists())
            createNewFolder(destination);

        deCompress(source, destination);
    }

    /**
     * Searches for files or folders with a regex in a folder you want.
     *
     * @param regex       Expression you want to search it in your desired folder.
     * @param destination Location you want to search in.
     * @return List of paths of files or folders which their names matches with given regex.
     */
    public List<String> search(String regex, String destination) {
        destination = pathSeparatorCorrector(destination);

        if (new java.io.File(destination).isFile())
            throw new CannotSearchInFileException(destination);

        return recursionSearch(regex, destination);
    }

    /**
     * Deletes your desired file or folder.
     *
     * @param destination Location of file or folder you want to delete.
     * @throws IOException If anything goes wrong in deleting your desired file or folder
     *                     an IOException will be thrown.
     */
    public void delete(String destination) throws IOException {
        destination = pathSeparatorCorrector(destination);

        java.io.File file = new java.io.File(destination);
        if (file.isFile()) {
            if (!file.delete())
                throw new IOException(//
                        "Failed to delete the file because: " +//
                                getReasonForFileDeletionFailureInPlainEnglish(file));
        } else
            deleteFolder(destination);
    }

    /**
     * Creates a new file in your desired location.
     *
     * @param destination Location which you want to create your new file in.
     * @throws IOException If anything goes wrong in creating a new file an IOException will be thrown.
     */
    public void createNewFile(String destination) throws IOException {
        destination = pathSeparatorCorrector(destination);

        if (isFileExist(destination))
            throw new FileAlreadyExistsException(destination);

        File.create(Paths.get(destination));
    }

    /**
     * Creates a new folder in your desired location.
     *
     * @param destination Location which you want to create your new folder in.
     * @throws IOException If anything goes wrong in creating a new folder an IOException will be thrown.
     */
    public void createNewFolder(String destination) throws IOException {
        destination = pathSeparatorCorrector(destination);

        if (isFileExist(destination))
            throw new FileAlreadyExistsException(destination);

        Folder.create(Paths.get(destination));
    }

    /**
     * Checks if your desired file or folder exist or not.
     *
     * @param destination Location of file or folder which you want to check its existence.
     * @return True if file exist in drive and false if its not.
     */
    public boolean isFileExist(String destination) {
        return new java.io.File(pathSeparatorCorrector(destination)).exists();
    }

    private boolean canNotItGoBackFromThisFolder(Folder location) {
        return location.equals(this.homeLocation);
    }

    private String pathSeparatorCorrector(String path) {
        return path.replaceAll("\\\\", "/");
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

    private void deCompress(String source, String destination) throws IOException {
        java.io.File destDir = new java.io.File(destination);
        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(source));
        ZipEntry zipEntry = zis.getNextEntry();
        while (zipEntry != null) {
            java.io.File newFile = extractFileFromZip(destDir, zipEntry);
            if (zipEntry.isDirectory()) {
                if (!newFile.isDirectory() && !newFile.mkdirs()) {
                    throw new IOException("Failed to create directory " + newFile);
                }
            } else {
                // fix for Windows-created archives
                java.io.File parent = newFile.getParentFile();
                if (!parent.isDirectory() && !parent.mkdirs()) {
                    throw new IOException("Failed to create directory " + parent);
                }

                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
            }
            zipEntry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();
    }

    private java.io.File extractFileFromZip(java.io.File destinationDir, ZipEntry zipEntry) throws IOException {
        java.io.File destFile = new java.io.File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + java.io.File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
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

    private List<String> recursionSearch(String regex, String destination) {
        Folder folder = new Folder(Paths.get(destination));
        List<String> foundedFiles = new ArrayList<>();
        Pattern p = Pattern.compile(regex);
        for (File file : folder.getContains()) {
            if (p.matcher(file.getName()).find())
                foundedFiles.add(file.getPath());
            if (file.isDirectory())
                foundedFiles.addAll(recursionSearch(regex, file.getPath()));
        }
        return foundedFiles;
    }

}
