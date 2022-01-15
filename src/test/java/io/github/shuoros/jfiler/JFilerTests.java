package io.github.shuoros.jfiler;

import io.github.shuoros.jcompressor.compress.ZipCompressor;
import io.github.shuoros.jfiler.exception.CannotSearchInFileException;
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
        assertNull(jFiler.getHomeLocation());
    }

    @Test
    @Order(2)
    public void JFilerMustOpenAHomeWithNoProblem() {
        // Given
        Folder folder = openFolder("JFilerCreatedSuccessfully");

        // When
        Folder home = jFiler.getHomeLocation();

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

        // When
        JFiler.delete(file);

        // Then
        assertFalse(file.exists());
    }

    @Test
    @Order(11)
    public void whenJFilerCompressesAFileItMustBeCompressed() {
        // Given
        File file = openFile("JFilerCreatedSuccessfully/file.txt");
        File zippedFile = openFile("JFilerCreatedSuccessfully/zipFile.zip");

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

        // When
        JFiler.extract(zipFile, extractDestination, new ZipCompressor());

        // Then
        assertTrue(extractedFile.exists());

        // After
        JFiler.delete(extractedFile);
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

        // When
        JFiler.createNewFile(file.getPath());

        // Then
        assertTrue(file.exists());

        // After
        JFiler.delete(file);
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

        // When
        JFiler.createNewFolder(folder.getPath());

        // Then
        assertTrue(folder.exists());

        // After
        JFiler.delete(folder);
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
