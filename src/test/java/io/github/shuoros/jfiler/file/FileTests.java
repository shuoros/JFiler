package io.github.shuoros.jfiler.file;

import io.github.shuoros.jfiler.JFiler;
import io.github.shuoros.jfiler.JFilerTests;
import org.junit.jupiter.api.*;

import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FileTests {

    private static String resource;
    private File file;

    @BeforeAll
    public static void beforeAll() throws Exception {
        resource = Paths.get(Objects.requireNonNull(JFilerTests.class.getResource("/")).toURI()).toFile().getPath();
    }

    @Test
    @Order(1)
    public void openMethodMustCreateANewInstanceOfFile() {
        // Given
        File file = openFile("JFilerCreatedSuccessfully/JFilerCreatedSuccessfully.txt");

        // When
        File openedFile = File.open(file.toPath());

        assertEquals(file, openedFile);
    }

    @Test
    @Order(2)
    public void whenFileExistsCreateFactoryMustThrowFileAlreadyExistsException() {
        // Given
        File file = openFile("JFilerCreatedSuccessfully/JFilerCreatedSuccessfully.txt");

        // Then
        assertThrows(FileAlreadyExistsException.class, () -> File.create(file.toPath()));
    }

    @Test
    @Order(3)
    public void getTypeMethodMustActualTypeOfFile() {
        // Given
        File file = openFile("JFilerCreatedSuccessfully/JFilerCreatedSuccessfully.txt");

        // When
        Type type = file.getType();

        // Then
        assertEquals(Type.TXT, type);
    }

    @Test
    @Order(4)
    public void getLocationMustReturnActualLocationOfFile() {
        // Given
        File file = openFile("JFilerCreatedSuccessfully/JFilerCreatedSuccessfully.txt");

        // When
        Path path = file.getLocation();

        // Then
        assertEquals(file.toPath(), path);
    }

    @Test
    @Order(5)
    public void getParentLocationMustReturnActualLocationOfParentOfFile() {
        // Given
        File file = openFile("JFilerCreatedSuccessfully/JFilerCreatedSuccessfully.txt");
        Folder folder = openFolder("JFilerCreatedSuccessfully");

        // When
        Folder parentFolder = file.getParentFolder();

        // Then
        assertEquals(folder, parentFolder);
    }

    @Test
    @Order(6)
    public void whenTwoFilesHaveSamePathTheEqualsMethodMustReturnTrue() {
        // Given
        File file1 = openFile("JFilerCreatedSuccessfully/JFilerCreatedSuccessfully.txt");
        File file2 = openFile("JFilerCreatedSuccessfully/JFilerCreatedSuccessfully.txt");

        // When
        boolean equals = file1.equals(file2);

        // Then
        assertTrue(equals);
    }

    @Test
    @Order(7)
    public void whenTwoFilesHaveNotSamePathTheEqualsMethodMustReturnFalse() {
        // Given
        File file1 = openFile("JFilerCreatedSuccessfully/JFilerCreatedSuccessfully.txt");
        File file2 = openFile("JFilerCreatedSuccessfully/InnerFolder/InnerFolder.txt");

        // When
        boolean equals = file1.equals(file2);

        // Then
        assertFalse(equals);
    }

    private File openFile(String location) {
        return new File(Paths.get(resource + "/" + location));
    }

    private Folder openFolder(String location) {
        return new Folder(Paths.get(resource + "/" + location));
    }

}
