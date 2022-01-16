package io.github.shuoros.jfiler;

import io.github.shuoros.jcompressor.JCompressor;
import io.github.shuoros.jfiler.exception.*;
import io.github.shuoros.jfiler.file.File;
import io.github.shuoros.jfiler.file.Folder;
import io.github.shuoros.jfiler.util.JFilerUtils;
import io.github.shuoros.jfiler.util.SystemOS;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

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

    private final Folder homeLocation;
    private final Stack<Folder> frontLocation = new Stack<>();
    private final Stack<Folder> rearLocation = new Stack<>();
    private boolean copy;
    private boolean cut;
    private File clipBoard;
    private Folder currentLocation;

    /**
     * Constructs a JFiler instance on the given location.
     */
    public JFiler() {
        this("/");
    }

    /**
     * Constructs a JFiler instance on the given location.
     *
     * @param location Home of JFiler instance which going to be created.
     */
    public JFiler(String location) {
        this.homeLocation = ("/".equals(location)) ? null : Folder.open(JFilerUtils.pathSeparatorCorrector(location));
        this.currentLocation = this.homeLocation;
        this.copy = false;
        this.cut = false;
    }

    public static JFiler open() {
        return new JFiler();
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
     * Creates a new instance of {@link io.github.shuoros.jfiler.file.File} in your desired location.
     *
     * @param location Location of file you want to get its instance.
     * @return New instance of File in your desired location.
     */
    public static File getFile(String location) {
        return File.open(location);
    }

    /**
     * Creates a new instance of {@link io.github.shuoros.jfiler.file.Folder} in your desired location.
     *
     * @param location Location of folder you want to get its instance.
     * @return New instance of Folder in your desired location.
     */
    public static Folder getFolder(String location) {
        return Folder.open(location);
    }

    /**
     * Hide the given file or folder. If Your machine is Windows its change the file or folders properties
     * to hide it and if your machine is based on unix or mac it will add a dot "." before your desired file
     * or folder's name.
     *
     * @param file Your desired file or folder to hide.
     * @throws IOException If anything goes wrong in changing the file properties or changing its name
     *                     an IOException will be thrown.
     */
    public static void hide(java.io.File file) throws IOException {
        hide(file.getPath());
    }

    /**
     * Hide the given file or folder. If Your machine is Windows its change the file or folders properties
     * to hide it and if your machine is based on unix or mac it will add a dot "." before your desired file
     * or folder's name.
     *
     * @param location Location of your desired file or folder to hide.
     * @throws IOException If anything goes wrong in changing the file properties or changing its name
     *                     an IOException will be thrown.
     */
    public static void hide(String location) throws IOException {
        location = JFilerUtils.pathSeparatorCorrector(location);

        if (File.isHidden(location))
            throw new FileIsAlreadyHideException(location);

        if (SystemOS.isUnix() || SystemOS.isMac())
            JFilerUtils.hideFileInUnix(location);
        else if (SystemOS.isWindows())
            JFilerUtils.hideFileInWindows(location);
    }

    /**
     * Un hides the given file or folder. If Your machine is Windows its change the file or folders properties
     * to un hide it and if your machine is based on unix or mac it will remove the dot "." before your desired
     * file or folder's name.
     *
     * @param file Your desired file or folder to un hide.
     * @throws IOException If anything goes wrong in changing the file properties or changing its name
     *                     an IOException will be thrown.
     */
    public static void unHide(java.io.File file) throws IOException {
        unHide(file.getPath());
    }

    /**
     * Un hides the given file or folder. If Your machine is Windows its change the file or folders properties
     * to un hide it and if your machine is based on unix or mac it will remove the dot "." before your desired
     * file or folder's name.
     *
     * @param location Location of your desired file or folder to un hide.
     * @throws IOException If anything goes wrong in changing the file properties or changing its name
     *                     an IOException will be thrown.
     */
    public static void unHide(String location) throws IOException {
        location = JFilerUtils.pathSeparatorCorrector(location);

        if (File.isVisible(location))
            throw new FileIsAlreadyVisibleException(location);

        if (SystemOS.isUnix() || SystemOS.isMac())
            JFilerUtils.unHideFileInUnix(location);
        else if (SystemOS.isWindows())
            JFilerUtils.unHideFileInWindows(location);
    }

    /**
     * Renames your desired file or folder to name you want.
     *
     * @param file    Your desired file or folder.
     * @param newName New name of Your desired file or folder.
     * @throws IOException If anything goes wrong in changing the name of your desired file or folder
     *                     an IOException will be thrown.
     */
    public static void rename(java.io.File file, String newName) throws IOException {
        rename(file.getPath(), newName);
    }

    /**
     * Renames your desired file or folder to name you want.
     *
     * @param location Location of your desired file or folder.
     * @param newName  New name of Your desired file or folder.
     * @throws IOException If anything goes wrong in changing the name of your desired file or folder
     *                     an IOException will be thrown.
     */
    public static void rename(String location, String newName) throws IOException {
        moveTo(location, JFilerUtils.newNamedLocation(location, newName));
    }

    /**
     * Move your desired file or folder in destination you want.
     *
     * @param source      Your desired file or folder.
     * @param destination Location which you want your file to be moved there.
     * @throws IOException If anything goes wrong in cutting your desired file or folder an IOException will be thrown.
     */
    public static void moveTo(java.io.File source, String destination) throws IOException {
        moveTo(source.getPath(), destination);
    }

    /**
     * Move your desired file or folder in destination you want.
     *
     * @param source      Location of your desired file or folder.
     * @param destination Location which you want your file to be moved there.
     * @throws IOException If anything goes wrong in cutting your desired file or folder an IOException will be thrown.
     */
    public static void moveTo(String source, String destination) throws IOException {
        copyTo(source, destination);
        deleteThe(source);
    }

    /**
     * Copy your desired file or folder in destination you want.
     *
     * @param source      Your desired file or folder.
     * @param destination Location which you want your file to be copy there.
     * @throws IOException If anything goes wrong in coping your desired file or folder an IOException will be thrown.
     */
    public static void copyTo(java.io.File source, String destination) throws IOException {
        copyTo(source.getPath(), destination);
    }

    /**
     * Copy your desired file or folder in destination you want.
     *
     * @param source      Location of your desired file or folder.
     * @param destination Location which you want your file to be copy there.
     * @throws IOException If anything goes wrong in coping your desired file or folder an IOException will be thrown.
     */
    public static void copyTo(String source, String destination) throws IOException {
        source = JFilerUtils.pathSeparatorCorrector(source);
        destination = JFilerUtils.pathSeparatorCorrector(destination);

        if (File.isFile(source))
            JFilerUtils.copyFile(source, destination);
        else
            JFilerUtils.copyFolder(source, destination);
    }

    /**
     * Compresses desired list of your files or folders into a zip file.
     *
     * @param locations               List of locations of your files or folder which you want to compress.
     * @param compressFileDestination Location of zip file to save.
     * @param compressor              Compress method
     */
    public static void compress(List<String> locations, String compressFileDestination, JCompressor compressor) {
        List<java.io.File> files = new ArrayList<>();
        for (String location : locations)
            files.add(new java.io.File(location));

        java.io.File toCompressFile = new java.io.File(compressFileDestination);
        compress(files, toCompressFile, compressor);
    }

    /**
     * Compresses your desired file or folder into a zip file.
     *
     * @param location                Your file or folder which you want to compress.
     * @param compressFileDestination Location of zip file to save.
     * @param compressor              Compress method
     */
    public static void compress(java.io.File location, java.io.File compressFileDestination, JCompressor compressor) {
        compressor.compress(List.of(location), compressFileDestination);
    }

    /**
     * Compresses desired list of your files or folders into a zip file.
     *
     * @param locations               List of locations of your files or folders which you want to compress.
     * @param compressFileDestination Location of zip file to save.
     * @param compressor              Compress method
     */
    public static void compress(List<java.io.File> locations, java.io.File compressFileDestination, JCompressor compressor) {
        compressor.compress(locations, compressFileDestination);
    }

    /**
     * Unzips your desired zip file in destination you want.
     *
     * @param source      Location of Your zip file.
     * @param destination Location of extracted files or folders from zip file to save.
     * @param extractor   Extract method.
     */
    public static void extract(String source, String destination, JCompressor extractor) {
        extract(new java.io.File(source), new java.io.File(destination), extractor);
    }

    /**
     * Unzips your desired zip file in destination you want.
     *
     * @param zipFile     Your zip file.
     * @param destination Location of extracted files or folders from zip file to save.
     * @param extractor   Extract method.
     */
    public static void extract(java.io.File zipFile, java.io.File destination, JCompressor extractor) {
        extractor.extract(zipFile, destination);
    }

    /**
     * Searches for files or folders with a regex in a folder you want.
     *
     * @param regex  Expression you want to search it in your desired folder.
     * @param folder Folder you want to search in.
     * @return List of paths of files or folders which their names matches with given regex.
     */
    public static List<String> search(String regex, Folder folder) {
        return search(regex, folder.getPath());
    }

    /**
     * Searches for files or folders with a regex in a folder you want.
     *
     * @param regex    Expression you want to search it in your desired folder.
     * @param location Location you want to search in.
     * @return List of paths of files or folders which their names matches with given regex.
     */
    public static List<String> search(String regex, String location) {
        location = JFilerUtils.pathSeparatorCorrector(location);

        if (File.isFile(location))
            throw new CannotSearchInFileException(location);

        return JFilerUtils.recursionSearch(regex, location);
    }

    /**
     * Deletes your desired file or folder.
     *
     * @param file File or folder you want to delete.
     * @throws IOException If anything goes wrong in deleting your desired file or folder
     *                     an IOException will be thrown.
     */
    public static void deleteThe(java.io.File file) throws IOException {
        deleteThe(file.getPath());
    }

    /**
     * Deletes your desired file or folder.
     *
     * @param location Location of file or folder you want to delete.
     * @throws IOException If anything goes wrong in deleting your desired file or folder
     *                     an IOException will be thrown.
     */
    public static void deleteThe(String location) throws IOException {
        location = JFilerUtils.pathSeparatorCorrector(location);

        if (File.isFile(location))
            JFilerUtils.deleteFile(location);
        else
            JFilerUtils.deleteFolder(location);
    }

    /**
     * Creates a new file in your desired location.
     *
     * @param location Location which you want to create your new file in.
     * @throws IOException If anything goes wrong in creating a new file an IOException will be thrown.
     */
    public static void createNewFile(String location) throws IOException {
        location = JFilerUtils.pathSeparatorCorrector(location);

        if (File.exists(location))
            throw new FileAlreadyExistsException(location);

        File.create(Paths.get(location));
    }

    /**
     * Creates a new folder in your desired location.
     *
     * @param location Location which you want to create your new folder in.
     * @throws IOException If anything goes wrong in creating a new folder an IOException will be thrown.
     */
    public static void createNewFolder(String location) throws IOException {
        location = JFilerUtils.pathSeparatorCorrector(location);

        if (File.exists(location))
            throw new FileAlreadyExistsException(location);

        Folder.create(Paths.get(location));
    }

    /**
     * Determines if given file or folder object exist on disk or not.
     *
     * @param file File or folder you want to check its existence.
     * @return True if file exist in drive and false if its not.
     */
    public static Boolean isFileExist(File file) {
        return file.exists();
    }

    /**
     * Determines if given file or folder object exist on disk or not.
     *
     * @param location Location of file or folder which you want to check its existence.
     * @return True if file exist in drive and false if its not.
     */
    public static Boolean isFileExist(String location) {
        return File.exists(location);
    }

    /**
     * Returns an instance of {@link io.github.shuoros.jfiler.file.Folder} which represent the home.
     *
     * @return An instance of Folder which represent the home.
     */
    public Folder getHome() {
        return this.homeLocation;
    }

    /**
     * Returns an instance of {@link io.github.shuoros.jfiler.file.Folder}
     * which represent the current location of JFiler.
     *
     * @return An instance of Folder which represent the current location of JFiler.
     */
    public Folder getCurrent() {
        return this.currentLocation;
    }

    /**
     * Returns an instance of {@link io.github.shuoros.jfiler.file.Folder}
     * which represent the rear location of JFiler.
     *
     * @return An instance of Folder which represent the rear location of JFiler.
     */
    public Folder getRear() {
        return this.rearLocation.peek();
    }

    /**
     * Returns an instance of {@link io.github.shuoros.jfiler.file.Folder}
     * which represent the front location of JFiler.
     *
     * @return An instance of Folder which represent the front location of JFiler.
     */
    public Folder getFront() {
        return this.frontLocation.peek();
    }

    /**
     * Returns an instance of {@link io.github.shuoros.jfiler.file.File} which represent
     * the current file or folder in clipboard.
     *
     * @return An instance of File which represent the current file or folder in clipboard.
     */
    public File getClipBoard() {
        return this.clipBoard;
    }

    /**
     * Returns a string that indicates the operation that the track is about to perform.
     * It can be a copy or a cut or nothing.
     *
     * @return A string that indicates the operation that the track is about to perform.
     * It can be a copy or a cut or NaN.
     */
    public String pasteOperation() {
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
        List<File> files = new ArrayList<>();

        if (null == this.currentLocation)
            for (java.io.File root : java.io.File.listRoots())
                files.add(new File(root.toPath()));
        else
            files.addAll(this.currentLocation.getContains());

        return files;
    }

    /**
     * Opens a folder in the given location in JFiler's current location. The current location will be added
     * to rear location and the folder you give to function will be set in current location. If home is locked
     * and the given location is outside of home it throws
     * {@link LocationNotFoundException}.
     *
     * @param location Location of desired folder which you want to open.
     */
    public void openFolder(String location) {
        location = InitialPreparationOfLocation(location);

        if (canNotOpenThis(location))
            throw new LocationNotFoundException(location);

        this.frontLocation.clear();
        this.rearLocation.push(this.currentLocation);
        this.currentLocation = Folder.open(location);
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
     * is outside of home it throws {@link LocationNotFoundException}.
     */
    public void goUp() {
        if (this.currentLocation == null || canNotGoUpFromThisFolder(this.currentLocation.getPath()))
            throw new LocationNotFoundException(null);

        this.frontLocation.push(this.currentLocation);
        if (currentLocationIsLastLocationToUp())
            this.currentLocation = null;
        else
            this.currentLocation = this.currentLocation.getParentFolder();
    }

    /**
     * Saves your desired file or folder in clipboard and set pasting operation to cut method.
     *
     * @param source Location of your desired file or folder you want to cut.
     */
    public void cut(String source) {
        source = InitialPreparationOfLocation(source);

        this.clipBoard = File.open(source);
        this.cut = true;
        this.copy = false;
    }

    /**
     * Saves your desired file or folder in clipboard and set pasting operation to copy method.
     *
     * @param source Location of your desired file or folder you want to copy.
     */
    public void copy(String source) {
        source = InitialPreparationOfLocation(source);

        this.clipBoard = File.open(source);
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
        destination = InitialPreparationOfLocation(destination);

        if (this.clipBoard != null)
            if (this.copy)
                copyTo(this.clipBoard.getPath(), destination);
            else if (this.cut)
                moveTo(this.clipBoard.getPath(), destination);

        this.clipBoard = null;
        this.copy = false;
        this.cut = false;
    }

    /**
     * Deletes your desired file or folder.
     *
     * @param destination Location of file or folder you want to delete.
     * @throws IOException If anything goes wrong in deleting file or folder an IOException will be thrown.
     */
    public void delete(String destination) throws IOException {
        destination = InitialPreparationOfLocation(destination);

        deleteThe(destination);
    }

    private Boolean currentLocationIsLastLocationToUp() {
        return this.currentLocation.getPath().split("(?<!:)/").length == 2;
    }

    private String InitialPreparationOfLocation(String location) {
        if (location.startsWith("/"))
            location = location.substring(1);
        if (this.homeLocation != null && !location.startsWith("/"))
            location = this.homeLocation.getPath().concat("/").concat(location);
        return JFilerUtils.pathSeparatorCorrector(location);
    }

    private boolean canNotOpenThis(String location) {
        return (this.homeLocation == null && !File.exists(location)) //
                || (this.homeLocation != null && !File.exists(location));
    }

    private boolean canNotGoUpFromThisFolder(String location) {
        return this.homeLocation != null && location.equals(this.homeLocation.getPath());
    }

}
