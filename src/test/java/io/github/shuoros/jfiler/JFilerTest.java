package io.github.shuoros.jfiler;

import io.github.shuoros.jfiler.file.File;
import io.github.shuoros.jfiler.file.Folder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class JFilerTest {

    private static String resource;
    private JFiler jFiler;

    @BeforeAll
    public static void beforeAll() throws Exception {
        resource = Paths.get(Objects.requireNonNull(JFilerTest.class.getResource("/")).toURI()).toFile().getPath();
    }

    @BeforeEach
    public void beforeEach() {
        jFiler = openJFiler("JFilerCreatedSuccessfully");
    }

    @Test
    public void JFilerMustOpenADirectoryWithNoProblem() {
        // Given
        Folder folder = openFolder("JFilerCreatedSuccessfully");

        // Then
        assertEquals(folder, jFiler.getHomeLocation());
    }

    @Test
    public void JFilerHomeLockMustOpenADirectoryWithLockedHome() {
        // Given
        jFiler = openJFilerInLockedHome("JFilerCreatedSuccessfully");

        // Then
        assertTrue(jFiler.isHomeLocked());
    }

    @Test
    public void getFileMethodMustReturnAValidFile() {
        // Given
        File file = openFile("JFilerCreatedSuccessfully/JFilerCreatedSuccessfully.txt");

        // When
        File openedFileByJFiler = JFiler.getFile(resource + "/JFilerCreatedSuccessfully/JFilerCreatedSuccessfully.txt");

        // Then
        assertEquals(file, openedFileByJFiler);
    }

    @Test
    public void getFolderMethodMustReturnAValidFolder() {
        // Given
        Folder folder = openFolder("JFilerCreatedSuccessfully");

        // When
        File openedFolderByJFiler = JFiler.getFolder(resource + "/JFilerCreatedSuccessfully");

        // Then
        assertEquals(folder, openedFolderByJFiler);

    }

    @Test
    public void getListMethodMustReturnListOfAllFilesInThatLocation() {
        // Given
        List<File> files = List.of(openFolder("JFilerCreatedSuccessfully/InnerFolder")//
                , openFile("JFilerCreatedSuccessfully/JFilerCreatedSuccessfully.txt"));

        // Then
        assertEquals(files, jFiler.getList());
    }

    @Test
    public void whenAFolderOpensByOpenFolderMethodTheCurrentLocationOfJFilerMustChangeToOpenedFolder() {
        // Given
        Folder folder = openFolder("JFilerCreatedSuccessfully/InnerFolder");

        // When
        jFiler.openFolder(folder.getPath());

        // Then
        assertEquals(folder, jFiler.getCurrentLocation());
    }

    @Test
    public void whenAFolderOpensByOpenFolderMethodTheCurrenLocationOfJFilerMustAddInTheRearLocationsStack() {
        // Given
        Folder folder = openFolder("JFilerCreatedSuccessfully/InnerFolder");
        Folder rearFolder = openFolder("JFilerCreatedSuccessfully");

        // When
        jFiler.openFolder(folder.getPath());

        // Then
        assertEquals(rearFolder, jFiler.getRearLocation());
    }

    @Test
    public void whenOpenFolderMethodCalledItMustOpenTheDesiredFolder() {
        // Given
        List<File> files = List.of(openFile("JFilerCreatedSuccessfully/InnerFolder/InnerFolder.txt"));
        Folder folder = openFolder("JFilerCreatedSuccessfully/InnerFolder");

        // When
        jFiler.openFolder(folder.getPath());

        // Then
        assertEquals(files, jFiler.getList());
    }

    @Test
    public void whenOpenFolderMethodCalledIfHomeWasLockedAndIfDesiredDestinationIsOutOfHomeLocationMustThrowHomeIsLockedException() {
        // Given
        jFiler = openJFilerInLockedHome("JFilerCreatedSuccessfully");
        Folder folder = new Folder(Paths.get("C:/Users/Soroush/Desktop"));

        // When
        Exception exception = assertThrows(RuntimeException.class, () -> jFiler.openFolder(folder.getPath()));

        // Then
        assertTrue(exception.getMessage().contains("You can't go back any further, Because the home is locked!"));
    }

    @Test
    public void whenGoBackwardFromCurrentLocationIfNoBackWardHistoryExistedMustThrowNoBackwardHistoryException() {
        // When
        Exception exception = assertThrows(RuntimeException.class, () -> jFiler.goBackward());

        // Then
        assertTrue(exception.getMessage().contains("You can't go backward, There is no history of rear folders!"));
    }

    @Test
    public void whenGoBackwardFromCurrentLocationItMustAddToFrontLocationStack(){
        // Given
        Folder folder = openFolder("JFilerCreatedSuccessfully/InnerFolder");

        // When
        jFiler.openFolder(folder.getPath());
        jFiler.goBackward();

        // Then
        assertEquals(folder, jFiler.getFrontLocation());
    }

    @Test
    public void whenGoBackwardFromCurrentLocationTheRearLocationMustSetInCurrentLocation(){
        // Given
        Folder folder = openFolder("JFilerCreatedSuccessfully/InnerFolder");
        Folder rearLocation;

        // When
        jFiler.openFolder(folder.getPath());
        rearLocation = jFiler.getRearLocation();
        jFiler.goBackward();

        // Then
        assertEquals(rearLocation, jFiler.getCurrentLocation());
    }

    @Test
    public void whenGoForwardFromCurrentLocationIfNoForWardHistoryExistedMustThrowNoForwardHistoryException() {
        // When
        Exception exception = assertThrows(RuntimeException.class, () -> jFiler.goForward());

        // Then
        assertTrue(exception.getMessage().contains("You can't go forward, There is no history of front folders!"));
    }

    @Test
    public void whenGoForwardFromCurrentLocationItMustAddToRearLocationStack(){
        // Given
        Folder folder = openFolder("JFilerCreatedSuccessfully/InnerFolder");
        Folder currentLocation;

        // When
        jFiler.openFolder(folder.getPath());
        jFiler.goBackward();
        currentLocation = jFiler.getCurrentLocation();
        jFiler.goForward();

        // Then
        assertEquals(currentLocation, jFiler.getRearLocation());
    }

    @Test
    public void whenGoForwardFromCurrentLocationTheFrontLocationMustSetInCurrentLocation(){
        // Given
        Folder folder = openFolder("JFilerCreatedSuccessfully/InnerFolder");
        Folder frontLocation;

        // When
        jFiler.openFolder(folder.getPath());
        jFiler.goBackward();
        frontLocation = jFiler.getFrontLocation();
        jFiler.goForward();

        // Then
        assertEquals(frontLocation, jFiler.getCurrentLocation());
    }

    @Test
    public void whenGoUpFromCurrentLocationWhenHomeIsLockedAndTheCurrentLocationEqualsHomeLocationMustThrowHomeIsLockedException(){
        // Given
        jFiler = openJFilerInLockedHome("JFilerCreatedSuccessfully");

        // When
        Exception exception = assertThrows(RuntimeException.class, () -> jFiler.goUp());

        // Then
        assertTrue(exception.getMessage().contains("You can't go back any further, Because the home is locked!"));
    }

    @Test
    public void whenGoUpFromCurrentLocationItMustAddToFrontLocationStack(){
        // Given
        Folder folder = openFolder("JFilerCreatedSuccessfully/InnerFolder");

        // When
        jFiler.openFolder(folder.getPath());
        jFiler.goUp();

        // Then
        assertEquals(folder, jFiler.getFrontLocation());
    }

    @Test
    public void whenGoUpFromCurrentLocationTheCurrentLocationsParentMustSetInCurrentLocation(){
        // Given
        Folder folder = openFolder("JFilerCreatedSuccessfully/InnerFolder/GoUpMethodTest");
        Folder upperLocationOfFolder = openFolder("JFilerCreatedSuccessfully/InnerFolder");

        // When
        jFiler.openFolder(folder.getPath());
        jFiler.goUp();

        // Then
        assertEquals(upperLocationOfFolder, jFiler.getCurrentLocation());
    }

    private JFiler openJFiler(String location) {
        return JFiler.open(resource + "/" + location);
    }

    private JFiler openJFilerInLockedHome(String location) {
        return JFiler.openInLockedHome(resource + "/" + location);
    }

    private File openFile(String location) {
        return new File(Paths.get(resource + "/" + location));
    }

    private Folder openFolder(String location) {
        return new Folder(Paths.get(resource + "/" + location));
    }
}
