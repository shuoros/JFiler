package io.github.shuoros.jfiler.file;

import io.github.shuoros.jfiler.JFilerTests;
import org.junit.jupiter.api.*;

import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FolderTests {

    private static String resource;

    @BeforeAll
    public static void beforeAll() throws Exception {
        resource = Paths.get(Objects.requireNonNull(JFilerTests.class.getResource("/")).toURI()).toFile().getPath();
    }

    @Test
    @Order(1)
    public void openMethodMustCreateANewInstanceOfFolder() {
        // Given
        Folder folder = openFolder("JFilerCreatedSuccessfully/InnerFolder");

        // When
        Folder openedFolder = Folder.open(folder.toPath());

        assertEquals(folder, openedFolder);
    }

    @Test
    @Order(2)
    public void whenAFolderExistsCreateFactoryMustThrowFileAlreadyExistsException() {
        // Given
        Folder folder = openFolder("JFilerCreatedSuccessfully/InnerFolder");

        // Then
        assertThrows(FileAlreadyExistsException.class, () -> Folder.create(folder.toPath()));
    }

    @Test
    @Order(3)
    public void containsMethodMustReturnAllFilesOfAFolder() {
        // Given
        List<File> files = List.of(openFolder("JFilerCreatedSuccessfully/CutCopyOperations")//
                , openFile("JFilerCreatedSuccessfully/HiddenFile.txt")//
                , openFile("JFilerCreatedSuccessfully/ImNotRenamed.txt")//
                , openFolder("JFilerCreatedSuccessfully/InnerFolder")//
                , openFile("JFilerCreatedSuccessfully/JFilerCreatedSuccessfully.txt")//
                , openFile("JFilerCreatedSuccessfully/VisibleFile.txt")//
                , openFolder("JFilerCreatedSuccessfully/ZipOperations"));
        Folder folder = openFolder("JFilerCreatedSuccessfully");

        // Then
        assertEquals(files, folder.getContains());
    }

    private File openFile(String location) {
        return new File(Paths.get(resource + "/" + location));
    }

    private Folder openFolder(String location) {
        return new Folder(Paths.get(resource + "/" + location));
    }

}
