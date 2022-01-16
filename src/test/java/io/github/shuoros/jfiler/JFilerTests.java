package io.github.shuoros.jfiler;

import io.github.shuoros.jcompressor.compress.ZipCompressor;
import io.github.shuoros.jfiler.exception.CannotSearchInFileException;
import io.github.shuoros.jfiler.exception.LocationNotFoundException;
import io.github.shuoros.jfiler.exception.NoBackwardHistoryException;
import io.github.shuoros.jfiler.exception.NoForwardHistoryException;
import io.github.shuoros.jfiler.file.File;
import io.github.shuoros.jfiler.file.Folder;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class JFilerTests {

    private static String resource;
    private JFiler jFiler;
    private File file;

    @BeforeAll
    public static void beforeAll() throws Exception {
        resource = Paths.get(//
                Objects.requireNonNull(//
                        JFilerTests.class.getResource("/")).toURI()).toFile().getPath();
    }

    @BeforeEach
    public void beforeEach() throws IOException {
        jFiler = openJFiler("JFilerCreatedSuccessfully");
        file = openFile("JFilerCreatedSuccessfully/file.txt");
    }

    @AfterEach
    public void afterEach() {
    }

    @Test
    @Order(1)
    public void JFilerMustNotHaveHomeLocationWhenItWasCreatedWithHomeAsRoot() {
        // When
        jFiler = JFiler.open();

        // Then
        assertNull(jFiler.getHome());
    }

    @Test
    @Order(2)
    public void JFilerMustOpenAHomeWithNoProblem() {
        // Given
        Folder folder = openFolder("JFilerCreatedSuccessfully");

        // When
        Folder home = jFiler.getHome();

        // Then
        assertEquals(folder, home);
    }

    @Test
    @Order(3)
    public void JFilerMustGetAFileObjectWithNoProblem() {
        // Given
        File file = openFile("JFilerCreatedSuccessfully/file.txt");

        // When
        File fileFromJFiler = JFiler.getFile(resource.concat("/JFilerCreatedSuccessfully/file.txt"));

        // Then
        assertEquals(file, fileFromJFiler);
    }

    @Test
    @Order(4)
    public void JFilerMustGetAFolderObjectWithNoProblem() {
        // Given
        Folder folder = openFolder("JFilerCreatedSuccessfully");

        // When
        Folder folderFromJFiler = JFiler.getFolder(resource.concat("/JFilerCreatedSuccessfully"));

        // Then
        assertEquals(folder, folderFromJFiler);
    }

    @Test
    @Order(5)
    public void whenJFilerHidesAFileItMustBeHidden() throws IOException {
        // Given
        File file = openFile("JFilerCreatedSuccessfully/file.txt");

        // When
        JFiler.hide(file);

        // Then
        assertTrue(file.isHidden());
    }

    @Test
    @Order(6)
    public void whenJFilerUnHidesAFileItMustBeVisible() throws IOException {
        // Given
        File file = openFile("JFilerCreatedSuccessfully/file.txt");

        // When
        JFiler.unHide(file);

        // Then
        assertFalse(file.isHidden());
    }

    @Test
    @Order(7)
    public void whenJFilerRenamesAFileItMustBeRenamed() throws IOException {
        // Given
        File file = openFile("JFilerCreatedSuccessfully/file.txt");
        File newFile = openFile("JFilerCreatedSuccessfully/newFile.txt");
        assertTrue(file.exists());
        assertFalse(newFile.exists());

        // When
        JFiler.rename(file, newFile.getName());

        // Then
        assertFalse(file.exists());
        assertTrue(newFile.exists());

        // After
        JFiler.rename(newFile, file.getName());
    }

    @Test
    @Order(8)
    public void whenJFilerMovesAFileToADestinationItMustBeMovedInToThatDestination() throws IOException {
        // Given
        File file = openFile("JFilerCreatedSuccessfully/file.txt");
        File movedFile = openFile("JFilerCreatedSuccessfully/move/file.txt");
        assertTrue(file.exists());
        assertFalse(movedFile.exists());

        // When
        JFiler.moveTo(file, movedFile.getPath());

        // Then
        assertFalse(file.exists());
        assertTrue(movedFile.exists());
    }

    @Test
    @Order(9)
    public void whenJFilerCopiesAFileToADestinationItMustBeCopiedInToThatDestination() throws IOException {
        // Given
        File file = openFile("JFilerCreatedSuccessfully/move/file.txt");
        File copiedFile = openFile("JFilerCreatedSuccessfully/file.txt");
        assertTrue(file.exists());
        assertFalse(copiedFile.exists());

        // When
        JFiler.copyTo(file, copiedFile.getPath());

        // Then
        assertTrue(copiedFile.exists());
    }

    @Test
    @Order(10)
    public void whenJFilerDeletesAFileItMustBeDeleted() throws IOException {
        // Given
        File file = openFile("JFilerCreatedSuccessfully/move/file.txt");
        assertTrue(file.exists());

        // When
        JFiler.deleteThe(file);

        // Then
        assertFalse(file.exists());
    }

    @Test
    @Order(11)
    public void whenJFilerCompressesAFileItMustBeCompressed() {
        // Given
        File file = openFile("JFilerCreatedSuccessfully/file.txt");
        File zippedFile = openFile("JFilerCreatedSuccessfully/zipFile.zip");
        assertFalse(zippedFile.exists());

        // When
        JFiler.compress(file, zippedFile, new ZipCompressor());

        // Then
        assertTrue(zippedFile.exists());
    }

    @Test
    @Order(12)
    public void whenJFilerExtractsAFileItMustBeExtracted() throws IOException {
        // Given
        File zipFile = openFile("JFilerCreatedSuccessfully/zipFile.zip");
        File extractedFile = openFile("JFilerCreatedSuccessfully/move/file.txt");
        Folder extractDestination = openFolder("JFilerCreatedSuccessfully/move");
        assertFalse(extractedFile.exists());

        // When
        JFiler.extract(zipFile, extractDestination, new ZipCompressor());

        // Then
        assertTrue(extractedFile.exists());

        // After
        JFiler.deleteThe(zipFile);
        JFiler.deleteThe(extractedFile);
    }

    @Test
    @Order(13)
    public void whenJFilerSearchesARegexInAFileItMustThrowCannotSearchInFileException() {
        // Given
        File file = openFile("JFilerCreatedSuccessfully/file.txt");
        String regex = ".txt$";

        // Then
        assertThrows(CannotSearchInFileException.class, () -> //
                JFiler.search(regex, file.getPath()));
    }

    @Test
    @Order(14)
    public void whenJFilerSearchesARegexInAFolderItMustFoundAListOfLocationsWhichMatchesWithRegex() {
        // Given
        Folder folder = openFolder("JFilerCreatedSuccessfully");
        String regex = ".txt$";
        List<String> expectedFiles = List.of(openFile("JFilerCreatedSuccessfully/file.txt").getPath());

        // When
        List<String> foundedFiles = JFiler.search(regex, folder);

        // Then
        assertEquals(expectedFiles, foundedFiles);
    }

    @Test
    @Order(15)
    public void whenJFilerCreatesNewFileIfThatFileExistedItMustThrowFileAlreadyExistsException() {
        // Given
        File file = openFile("JFilerCreatedSuccessfully/file.txt");

        // Then
        assertThrows(FileAlreadyExistsException.class, () -> //
                JFiler.createNewFile(file.getPath()));
    }

    @Test
    @Order(16)
    public void whenJFilerCreatesNewFileItMustBeCreated() throws IOException {
        // Given
        File file = openFile("JFilerCreatedSuccessfully/newFile.txt");
        assertFalse(file.exists());

        // When
        JFiler.createNewFile(file.getPath());

        // Then
        assertTrue(file.exists());

        // After
        JFiler.deleteThe(file);
    }

    @Test
    @Order(17)
    public void whenJFilerCreatesNewFolderIfThatFolderExistedItMustThrowFileAlreadyExistsException() {
        // Given
        Folder folder = openFolder("JFilerCreatedSuccessfully/file.txt");

        // Then
        assertThrows(FileAlreadyExistsException.class, () -> //
                JFiler.createNewFolder(folder.getPath()));
    }

    @Test
    @Order(18)
    public void whenJFilerCreatesNewFolderItMustBeCreated() throws IOException {
        // Given
        Folder folder = openFolder("JFilerCreatedSuccessfully/newFolder");
        assertFalse(folder.exists());

        // When
        JFiler.createNewFolder(folder.getPath());

        // Then
        assertTrue(folder.exists());

        // After
        JFiler.deleteThe(folder);
    }

    @Test
    @Order(19)
    public void whenJFilerChecksForExistenceOfAFileItMustReturnTrueIfThatFileIsExisted() {
        // Given
        File file = openFile("JFilerCreatedSuccessfully/file.txt");

        // When
        Boolean exist = JFiler.isFileExist(file);

        // Then
        assertTrue(exist);
    }

    @Test
    @Order(20)
    public void whenJFilerChecksForExistenceOfAFileItMustReturnFalseIfThatFileIsNotExisted() {
        // Given
        File file = openFile("JFilerCreatedSuccessfully/somefile.txt");

        // When
        Boolean exist = JFiler.isFileExist(file);

        // Then
        assertFalse(exist);
    }

    @Test
    @Order(21)
    public void whenAJFilerInstanceCreatedTheCurrentLocationMustBeInHomeLocation() {
        // Given
        Folder currentLocation = jFiler.getCurrent();

        // Then
        assertEquals(currentLocation, jFiler.getHome());
    }

    @Test
    @Order(22)
    public void JFilersGetListMustReturnLustOfFilesAndFoldersInCurrentLocation() {
        // Given
        List<File> filesAndFoldersOfCurrentLocation = List.of(//
                openFile("JFilerCreatedSuccessfully/file.txt"), //
                openFile("JFilerCreatedSuccessfully/move"));

        // Then
        assertEquals(filesAndFoldersOfCurrentLocation, jFiler.getList());
    }

    @Test
    @Order(23)
    public void whenJFilersOpensAFolderThatFolderMustBeSetToCurrentLocation() {
        // Given
        Folder newCurrentLocation = openFolder("JFilerCreatedSuccessfully/move");

        // When
        jFiler.openFolder("/move");

        // Then
        assertEquals(newCurrentLocation, jFiler.getCurrent());
    }

    @Test
    @Order(24)
    public void whenJFilerOpensAFolderCurrentLocationMustBeSetToRearLocation() {
        // Given
        Folder oldCurrentLocation = jFiler.getCurrent();

        // When
        jFiler.openFolder("/move");

        // Then
        assertEquals(oldCurrentLocation, jFiler.getRear());
    }

    @Test
    @Order(25)
    public void whenJFilerGoesBackwardIfThereIsNoRearLocationItMustThrowNoBackwardHistoryException() {
        assertThrows(NoBackwardHistoryException.class, () -> //
                jFiler.goBackward());
    }

    @Test
    @Order(26)
    public void whenJFilerOpensAFolderAndThenGoesBackWardTheCurrentLocationMustBeLastRearLocation() {
        // Given
        jFiler.openFolder("/move");
        Folder lastRearLocation = jFiler.getRear();

        // When
        jFiler.goBackward();

        // Then
        assertEquals(lastRearLocation, jFiler.getCurrent());
    }

    @Test
    @Order(27)
    public void whenJFilerGoesForwardIfThereIsNoFrontLocationItMustThrowNoForwardHistoryException() {
        assertThrows(NoForwardHistoryException.class, () -> //
                jFiler.goForward());
    }

    @Test
    @Order(28)
    public void whenJFilerOpensAFolderAndThenGoesBackWardAndThenGoesForwardTheCurrentLocationMustBeLastFrontLocation() {
        // Given
        jFiler.openFolder("/move");
        jFiler.goBackward();
        Folder lastFrontLocation = jFiler.getFront();

        // When
        jFiler.goForward();

        // Then
        assertEquals(lastFrontLocation, jFiler.getCurrent());
    }

    @Test
    @Order(29)
    public void whenJFilerOpensInRootGoUpMethodMustThrowLocationNotFoundException() {
        // Given
        jFiler = JFiler.open();

        // Then
        assertThrows(LocationNotFoundException.class, () -> //
                jFiler.goUp());
    }

    @Test
    @Order(30)
    public void whenJFilerIsInItsHomeLocationGoUpMethodMustThrowLocationNotFoundException() {
        assertThrows(LocationNotFoundException.class, () -> //
                jFiler.goUp());
    }

    @Test
    @Order(31)
    public void whenJFilerOpensAFolderWhenItGoesUpTheCurrentLocationMustBeParentOfCurrentLocation() {
        // Given
        jFiler.openFolder("/move");
        Folder parentOfCurrentLocation = openFolder("JFilerCreatedSuccessfully");

        // When
        jFiler.goUp();

        // Then
        assertEquals(parentOfCurrentLocation, jFiler.getCurrent());
    }

    @Test
    @Order(32)
    public void whenJFilerCutsAFileOrFolderThePasteOperationMustSetToPASTE() {
        // When
        jFiler.cut("/file.txt");

        // Then
        assertEquals("cut", jFiler.pasteOperation());
    }

    @Test
    @Order(33)
    public void whenJFilerCopiesAFileOrFolderThePasteOperationMustSetToPASTE() {
        // When
        jFiler.copy("/file.txt");

        // Then
        assertEquals("copy", jFiler.pasteOperation());
    }

    @Test
    @Order(34)
    public void whenJFilerCutsAFileOrFolderThatFileOrFolderMustSetToClipBoard() {
        // Given
        File cutedFile = openFile("JFilerCreatedSuccessfully/file.txt");

        // When
        jFiler.cut("/file.txt");

        // Then
        assertEquals(cutedFile, jFiler.getClipBoard());
    }

    @Test
    @Order(35)
    public void whenJFilerCopiesAFileOrFolderThatFileOrFolderMustSetToClipBoard() {
        // Given
        File copiedFile = openFile("JFilerCreatedSuccessfully/file.txt");

        // When
        jFiler.copy("/file.txt");

        // Then
        assertEquals(copiedFile, jFiler.getClipBoard());
    }

    @Test
    @Order(36)
    public void whenJFilerCutsAFileThePasteMethodMustCutItInToDestination() throws IOException {
        // Given
        File file = openFile("JFilerCreatedSuccessfully/file.txt");
        File movedFile = openFile("JFilerCreatedSuccessfully/move/file.txt");
        assertTrue(file.exists());
        assertFalse(movedFile.exists());

        // When
        jFiler.cut("/file.txt");
        jFiler.paste("/move/file.txt");

        // Then
        assertTrue(movedFile.exists());
        assertFalse(file.exists());
    }

    @Test
    @Order(37)
    public void whenJFilerCopiesAFileThePasteMethodMustCopyItInToDestination() throws IOException {
        // Given
        File file = openFile("JFilerCreatedSuccessfully/move/file.txt");
        File copiedFile = openFile("JFilerCreatedSuccessfully/file.txt");
        assertTrue(file.exists());
        assertFalse(copiedFile.exists());

        // When
        jFiler.copy("/move/file.txt");
        jFiler.paste("/file.txt");

        // Then
        assertTrue(copiedFile.exists());
    }

    @Test
    @Order(38)
    public void whenJFilerDeletesAFileOrFolderItMustBeDeleted() throws IOException {
        // Given
        File deletedFile = openFile("JFilerCreatedSuccessfully/move/file.txt");
        assertTrue(deletedFile.exists());

        // When
        jFiler.delete("/move/file.txt");

        // Then
        assertFalse(deletedFile.exists());
    }

    private JFiler openJFiler(String location) {
        return JFiler.open(resource + "/" + location);
    }

    private File openFile(String location) {
        return new File(Paths.get(resource + "/" + location));
    }

    private Folder openFolder(String location) {
        return new Folder(Paths.get(resource + "/" + location));
    }

}
