package io.github.shuoros.jfiler.file;

import io.github.shuoros.jfiler.JFilerTests;
import io.github.shuoros.jfiler.util.SystemOS;
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
        Folder folder = openFolder("JFilerCreatedSuccessfully/move");

        // When
        Folder openedFolder = Folder.open(folder.toPath());

        assertEquals(folder, openedFolder);
    }

    @Test
    @Order(2)
    public void whenAFolderExistsCreateFactoryMustThrowFileAlreadyExistsException() {
        // Given
        Folder folder = openFolder("JFilerCreatedSuccessfully/move");

        // Then
        assertThrows(FileAlreadyExistsException.class, () -> Folder.create(folder.toPath()));
    }

    @Test
    @Order(3)
    @Disabled
    public void containsMethodMustReturnAllFilesOfAFolder() {
        // Given
        List<File> files;
        if (SystemOS.isWindows())
            files = List.of(//
                    openFile("JFilerCreatedSuccessfully/file.txt"), //
                    openFolder("JFilerCreatedSuccessfully/move"));
        else
            files = List.of(//
                    openFolder("JFilerCreatedSuccessfully/move"), //
                    openFile("JFilerCreatedSuccessfully/file.txt"));
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
