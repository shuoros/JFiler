package io.github.shuoros.jfiler;

import io.github.shuoros.jfiler.exception.CannotSearchInFileException;
import io.github.shuoros.jfiler.file.File;
import io.github.shuoros.jfiler.file.Folder;
import org.junit.jupiter.api.*;

import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class JFilerTests {

    private static String resource;
    private JFiler jFiler;

    @BeforeAll
    public static void beforeAll() throws Exception {
        resource = Paths.get(Objects.requireNonNull(JFilerTests.class.getResource("/")).toURI()).toFile().getPath();
    }

    @BeforeEach
    public void beforeEach() {
        jFiler = openJFiler("JFilerCreatedSuccessfully");
    }

    @Test
    @Order(1)
    public void JFilerMustOpenADirectoryWithNoProblem() {
        // Given
        Folder folder = openFolder("JFilerCreatedSuccessfully");

        // Then
        assertEquals(folder, jFiler.getHomeLocation());
    }

    @Test
    @Order(2)
    public void JFilerHomeLockMustOpenADirectoryWithLockedHome() {
        // Given
        jFiler = openJFilerInLockedHome("JFilerCreatedSuccessfully");

        // Then
        assertTrue(jFiler.isHomeLocked());
    }

    @Test
    @Order(3)
    public void getFileMethodMustReturnAValidFile() {
        // Given
        File file = openFile("JFilerCreatedSuccessfully/JFilerCreatedSuccessfully.txt");

        // When
        File openedFileByJFiler = JFiler.getFile(resource + "/JFilerCreatedSuccessfully/JFilerCreatedSuccessfully.txt");

        // Then
        assertEquals(file, openedFileByJFiler);
    }

    @Test
    @Order(4)
    public void getFolderMethodMustReturnAValidFolder() {
        // Given
        Folder folder = openFolder("JFilerCreatedSuccessfully");

        // When
        File openedFolderByJFiler = JFiler.getFolder(resource + "/JFilerCreatedSuccessfully");

        // Then
        assertEquals(folder, openedFolderByJFiler);

    }

    @Test
    @Order(5)
    public void getListMethodMustReturnListOfAllFilesInThatLocation() {
        // Given
        List<File> files = List.of(openFolder("JFilerCreatedSuccessfully/CutCopyOperations")//
                , openFile("JFilerCreatedSuccessfully/HiddenFile.txt")//
                , openFile("JFilerCreatedSuccessfully/ImNotRenamed.txt")//
                , openFolder("JFilerCreatedSuccessfully/InnerFolder")//
                , openFile("JFilerCreatedSuccessfully/JFilerCreatedSuccessfully.txt")//
                , openFile("JFilerCreatedSuccessfully/VisibleFile.txt")//
                , openFolder("JFilerCreatedSuccessfully/ZipOperations"));

        // Then
        assertEquals(files, jFiler.getList());
    }

    @Test
    @Order(6)
    public void whenAFolderOpensByOpenFolderMethodTheCurrentLocationOfJFilerMustChangeToOpenedFolder() {
        // Given
        Folder folder = openFolder("JFilerCreatedSuccessfully/InnerFolder");

        // When
        jFiler.openFolder(folder.getPath());

        // Then
        assertEquals(folder, jFiler.getCurrentLocation());
    }

    @Test
    @Order(7)
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
    @Order(8)
    public void whenOpenFolderMethodCalledItMustOpenTheDesiredFolder() {
        // Given
        List<File> files = List.of(openFolder("JFilerCreatedSuccessfully/InnerFolder/GoUpMethodTest")//
                , openFile("JFilerCreatedSuccessfully/InnerFolder/InnerFolder.txt")//
                , openFile("JFilerCreatedSuccessfully/InnerFolder/SearchMe.java"));
        Folder folder = openFolder("JFilerCreatedSuccessfully/InnerFolder");

        // When
        jFiler.openFolder(folder.getPath());

        // Then
        assertEquals(files, jFiler.getList());
    }

    @Test
    @Order(9)
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
    @Order(10)
    public void whenGoBackwardFromCurrentLocationIfNoBackWardHistoryExistedMustThrowNoBackwardHistoryException() {
        // When
        Exception exception = assertThrows(RuntimeException.class, () -> jFiler.goBackward());

        // Then
        assertTrue(exception.getMessage().contains("You can't go backward, There is no history of rear folders!"));
    }

    @Test
    @Order(11)
    public void whenGoBackwardFromCurrentLocationItMustAddToFrontLocationStack() {
        // Given
        Folder folder = openFolder("JFilerCreatedSuccessfully/InnerFolder");

        // When
        jFiler.openFolder(folder.getPath());
        jFiler.goBackward();

        // Then
        assertEquals(folder, jFiler.getFrontLocation());
    }

    @Test
    @Order(12)
    public void whenGoBackwardFromCurrentLocationTheRearLocationMustSetInCurrentLocation() {
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
    @Order(13)
    public void whenGoForwardFromCurrentLocationIfNoForWardHistoryExistedMustThrowNoForwardHistoryException() {
        // When
        Exception exception = assertThrows(RuntimeException.class, () -> jFiler.goForward());

        // Then
        assertTrue(exception.getMessage().contains("You can't go forward, There is no history of front folders!"));
    }

    @Test
    @Order(14)
    public void whenGoForwardFromCurrentLocationItMustAddToRearLocationStack() {
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
    @Order(15)
    public void whenGoForwardFromCurrentLocationTheFrontLocationMustSetInCurrentLocation() {
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
    @Order(16)
    public void whenGoUpFromCurrentLocationWhenHomeIsLockedAndTheCurrentLocationEqualsHomeLocationMustThrowHomeIsLockedException() {
        // Given
        jFiler = openJFilerInLockedHome("JFilerCreatedSuccessfully");

        // When
        Exception exception = assertThrows(RuntimeException.class, () -> jFiler.goUp());

        // Then
        assertTrue(exception.getMessage().contains("You can't go back any further, Because the home is locked!"));
    }

    @Test
    @Order(17)
    public void whenGoUpFromCurrentLocationItMustAddToFrontLocationStack() {
        // Given
        Folder folder = openFolder("JFilerCreatedSuccessfully/InnerFolder");

        // When
        jFiler.openFolder(folder.getPath());
        jFiler.goUp();

        // Then
        assertEquals(folder, jFiler.getFrontLocation());
    }

    @Test
    @Order(18)
    public void whenGoUpFromCurrentLocationTheCurrentLocationsParentMustSetInCurrentLocation() {
        // Given
        Folder folder = openFolder("JFilerCreatedSuccessfully/InnerFolder/GoUpMethodTest");
        Folder upperLocationOfFolder = openFolder("JFilerCreatedSuccessfully/InnerFolder");

        // When
        jFiler.openFolder(folder.getPath());
        jFiler.goUp();

        // Then
        assertEquals(upperLocationOfFolder, jFiler.getCurrentLocation());
    }

    @Test
    @Order(19)
    public void whenHideAHiddenFileItMustThrowFileIsAlreadyHideException() throws Exception {
        // Given
        jFiler.hide(resource + "/JFilerCreatedSuccessfully/HiddenFile.txt");

        // When
        Exception exception = assertThrows(RuntimeException.class, () -> //
                jFiler.hide(resource + "/JFilerCreatedSuccessfully/HiddenFile.txt"));

        // Then
        assertTrue(exception.getMessage().contains("/JFilerCreatedSuccessfully/HiddenFile.txt"));
    }

    @Test
    @Order(20)
    public void whenHideAFileItMustBeHidden() throws Exception {
        // Given
        File mustBeHideFile = openFile("JFilerCreatedSuccessfully/VisibleFile.txt");

        // When
        jFiler.hide(mustBeHideFile.getPath());

        // Then
        assertTrue(mustBeHideFile.isHidden());

        // After
        jFiler.unHide(mustBeHideFile.getPath());
    }

    @Test
    @Order(21)
    public void whenUnHideAVisibleFileItMustThrowFileIsAlreadyVisibleException() {
        // When
        Exception exception = assertThrows(RuntimeException.class, () -> //
                jFiler.unHide(resource + "/JFilerCreatedSuccessfully/VisibleFile.txt"));

        // Then
        assertTrue(exception.getMessage().contains("/JFilerCreatedSuccessfully/VisibleFile.txt"));
    }

    @Test
    @Order(22)
    public void whenUnHideAFileItMustBeVisible() throws Exception {
        // Given
        File mustBeVisibleFile = openFile("JFilerCreatedSuccessfully/HiddenFile.txt");

        // When
        jFiler.unHide(mustBeVisibleFile.getPath());

        // Then
        assertFalse(mustBeVisibleFile.isHidden());

        // After
        jFiler.hide(mustBeVisibleFile.getPath());
    }

    @Test
    @Order(23)
    @Disabled
    public void whenRenameAFileItMustBeRenamed() throws Exception {
        // Given
        File mustBeRenameFile = openFile("JFilerCreatedSuccessfully/ImNotRenamed.txt");
        String newName = "ImRenamed.txt";

        // When
        jFiler.rename(mustBeRenameFile.getPath(), newName);

        // Then
        assertEquals(newName, mustBeRenameFile.getName());

        // After
        jFiler.rename(mustBeRenameFile.getPath(), "ImNotRenamed.txt");
    }

    @Test
    @Order(24)
    public void whenCutAFileToDestinationItMustBeCutIntoThatDestination() throws Exception {
        // Given
        File mustBeCutFile = openFile("JFilerCreatedSuccessfully/CutCopyOperations/CutTo.txt");
        String destination = resource + "/JFilerCreatedSuccessfully/CutCopyOperations/Cut/CutTo.txt";

        // When
        jFiler.cutTo(mustBeCutFile.getPath(), destination);

        // Then
        assertTrue(new File(Paths.get(destination)).exists());
    }

    @Test
    @Order(25)
    public void whenCutAFileToDestinationItMustNotBePresentInSource() throws Exception {
        // Given
        File mustBeCutFile = openFile("JFilerCreatedSuccessfully/CutCopyOperations/Cut/CutTo.txt");
        String destination = resource + "/JFilerCreatedSuccessfully/CutCopyOperations/CutTo.txt";

        // When
        jFiler.cutTo(mustBeCutFile.getPath(), destination);

        // Then
        assertFalse(mustBeCutFile.exists());
    }

    @Test
    @Order(26)
    public void whenCutAFolderToDestinationItMustBeCutIntoThatDestination() throws Exception {
        // Given
        Folder mustBeCutFile = openFolder("JFilerCreatedSuccessfully/CutCopyOperations/CutTo");
        String destination = resource + "/JFilerCreatedSuccessfully/CutCopyOperations/Cut/CutTo";

        // When
        jFiler.cutTo(mustBeCutFile.getPath(), destination);

        // Then
        assertTrue(new Folder(Paths.get(destination)).exists());
    }

    @Test
    @Order(27)
    public void whenCutAFolderToDestinationItMustBeCutIntoThatDestinationWithAllOfItsSubFilesAndFolders() throws Exception {
        // Given
        List<File> files = List.of(openFolder("JFilerCreatedSuccessfully/CutCopyOperations/Cut/CutTo/nothing")//
                , openFile("JFilerCreatedSuccessfully/CutCopyOperations/Cut/CutTo/nothing.txt"));
        Folder cutFile = openFolder("JFilerCreatedSuccessfully/CutCopyOperations/Cut/CutTo");

        // When
        // cuted in 25th test

        // Then
        assertEquals(files, cutFile.getContains());
    }

    @Test
    @Order(28)
    public void whenCutAFolderToDestinationItMustNotBePresentInSource() throws Exception {
        // Given
        Folder mustBeCutFolder = openFolder("JFilerCreatedSuccessfully/CutCopyOperations/Cut/CutTo");
        String destination = resource + "/JFilerCreatedSuccessfully/CutCopyOperations/CutTo";

        // When
        jFiler.cutTo(mustBeCutFolder.getPath(), destination);

        // Then
        assertFalse(mustBeCutFolder.exists());
    }

    @Test
    @Order(29)
    public void whenCutAFileItMustBeSetToClipBoard() {
        // Given
        File mustBeCutFile = openFile("JFilerCreatedSuccessfully/CutCopyOperations/CutTo.txt");

        // When
        jFiler.cut(mustBeCutFile.getPath());

        // Then
        assertEquals(mustBeCutFile, jFiler.getClipBoard());
    }

    @Test
    @Order(30)
    public void whenCutAFileThePasteOperationMustBeSetToCut() {
        // Given
        File mustBeCutFile = openFile("JFilerCreatedSuccessfully/CutCopyOperations/CutTo.txt");

        // When
        jFiler.cut(mustBeCutFile.getPath());

        // Then
        assertEquals("cut", jFiler.getPasteOperation());
    }

    @Test
    @Order(31)
    public void whenCutAFileThePasteMethodMustCutItInToDestination() throws Exception {
        // Given
        File mustBeCutFile = openFile("JFilerCreatedSuccessfully/CutCopyOperations/CutTo.txt");
        String destination = resource + "/JFilerCreatedSuccessfully/CutCopyOperations/Cut/CutTo.txt";

        // When
        jFiler.cut(mustBeCutFile.getPath());
        jFiler.paste(destination);

        // Then
        assertTrue(new File(Paths.get(destination)).exists());

        // After
        jFiler.cutTo(destination, mustBeCutFile.getPath());
    }

    @Test
    @Order(32)
    public void whenCopyAFileToDestinationItMustBeCopyIntoThatDestination() throws Exception {
        // Given
        File mustBeCopyFile = openFile("JFilerCreatedSuccessfully/CutCopyOperations/CopyTo.txt");
        String destination = resource + "/JFilerCreatedSuccessfully/CutCopyOperations/Copy/CopyTo.txt";

        // When
        jFiler.copyTo(mustBeCopyFile.getPath(), destination);

        // Then
        assertTrue(new File(Paths.get(destination)).exists());

        // After
        jFiler.delete(destination);
    }

    @Test
    @Order(33)
    public void whenCopyAFileToDestinationItMustBePresentInSource() throws Exception {
        // Given
        File mustBeCopyFile = openFile("JFilerCreatedSuccessfully/CutCopyOperations/CopyTo.txt");
        String destination = resource + "/JFilerCreatedSuccessfully/CutCopyOperations/Copy/CopyTo.txt";

        // When
        jFiler.copyTo(mustBeCopyFile.getPath(), destination);

        // Then
        assertTrue(mustBeCopyFile.exists());

        // After
        jFiler.delete(destination);
    }

    @Test
    @Order(34)
    public void whenCopyAFolderToDestinationItMustBeCopyIntoThatDestination() throws Exception {
        // Given
        Folder mustBeCopyFolder = openFolder("JFilerCreatedSuccessfully/CutCopyOperations/CopyTo");
        String destination = resource + "/JFilerCreatedSuccessfully/CutCopyOperations/Copy/CopyTo";

        // When
        jFiler.copyTo(mustBeCopyFolder.getPath(), destination);

        // Then
        assertTrue(new File(Paths.get(destination)).exists());

        // After
        jFiler.delete(destination);
    }

    @Test
    @Order(35)
    public void whenCopyAFolderToDestinationItMustBePresentInSource() throws Exception {
        // Given
        Folder mustBeCopyFolder = openFolder("JFilerCreatedSuccessfully/CutCopyOperations/CopyTo");
        String destination = resource + "/JFilerCreatedSuccessfully/CutCopyOperations/Copy/CopyTo";

        // When
        jFiler.copyTo(mustBeCopyFolder.getPath(), destination);

        // Then
        assertTrue(mustBeCopyFolder.exists());

        // After
        jFiler.delete(destination);
    }

    @Test
    @Order(36)
    public void whenCopyAFileItMustBeSetToClipBoard() {
        // Given
        File mustBeCopyFile = openFile("JFilerCreatedSuccessfully/CutCopyOperations/CopyTo.txt");

        // When
        jFiler.cut(mustBeCopyFile.getPath());

        // Then
        assertEquals(mustBeCopyFile, jFiler.getClipBoard());
    }

    @Test
    @Order(37)
    public void whenCopyAFileThePasteOperationMustBeSetToCopy() {
        // Given
        File mustBeCopyFile = openFile("JFilerCreatedSuccessfully/CutCopyOperations/CopyTo.txt");

        // When
        jFiler.copy(mustBeCopyFile.getPath());

        // Then
        assertEquals("copy", jFiler.getPasteOperation());
    }

    @Test
    @Order(38)
    public void whenCopyAFileThePasteMethodMustCopyItInToDestination() throws Exception {
        // Given
        File mustBeCopyFile = openFile("JFilerCreatedSuccessfully/CutCopyOperations/CopyTo.txt");
        String destination = resource + "/JFilerCreatedSuccessfully/CutCopyOperations/Copy/CopyTo.txt";

        // When
        jFiler.copy(mustBeCopyFile.getPath());
        jFiler.paste(destination);

        // Then
        assertTrue(new File(Paths.get(destination)).exists());

        // After
        jFiler.delete(destination);
    }

    @Test
    @Order(39)
    public void whenZipAFileItMustBeZipped() throws Exception {
        // Given
        File file = openFile("JFilerCreatedSuccessfully/ZipOperations/IWantToBeZipped1.txt");
        String destination = resource + "/JFilerCreatedSuccessfully/ZipOperations/zipped.zip";

        // When
        jFiler.zip(List.of(file.getPath()), destination);

        // Then
        assertTrue(new File(Paths.get(destination)).exists());

        // After
        jFiler.delete(destination);
    }

    @Test
    @Order(40)
    public void whenZipMultipleFilesTheirMustBeZipped() throws Exception {
        // Given
        File file1 = openFile("JFilerCreatedSuccessfully/ZipOperations/IWantToBeZipped1.txt");
        File file2 = openFile("JFilerCreatedSuccessfully/ZipOperations/IWantToBeZipped2.txt");
        String destination = resource + "/JFilerCreatedSuccessfully/ZipOperations/zipped.zip";

        // When
        jFiler.zip(List.of(file1.getPath(), file2.getPath()), destination);

        // Then
        assertTrue(new File(Paths.get(destination)).exists());

        // After
        jFiler.delete(destination);
    }

    @Test
    @Order(41)
    public void whenZipAFolderItMustBeZipped() throws Exception {
        // Given
        Folder folder = openFolder("JFilerCreatedSuccessfully/ZipOperations/IWantToBeZipped1");
        String destination = resource + "/JFilerCreatedSuccessfully/ZipOperations/zipped.zip";

        // When
        jFiler.zip(List.of(folder.getPath()), destination);

        // Then
        assertTrue(new File(Paths.get(destination)).exists());

        // After
        jFiler.delete(destination);
    }

    @Test
    @Order(42)
    public void whenZipMultipleFoldersTheirMustBeZipped() throws Exception {
        // Given
        Folder folder1 = openFolder("JFilerCreatedSuccessfully/ZipOperations/IWantToBeZipped1");
        Folder folder2 = openFolder("JFilerCreatedSuccessfully/ZipOperations/IWantToBeZipped2");
        String destination = resource + "/JFilerCreatedSuccessfully/ZipOperations/zipped.zip";

        // When
        jFiler.zip(List.of(folder1.getPath(), folder2.getPath()), destination);

        // Then
        assertTrue(new File(Paths.get(destination)).exists());

        // After
        jFiler.delete(destination);
    }

    @Test
    @Order(43)
    public void whenZipMultipleFilesAndFoldersTheirMustBeZipped() throws Exception {
        // Given
        File file1 = openFile("JFilerCreatedSuccessfully/ZipOperations/IWantToBeZipped1.txt");
        File file2 = openFile("JFilerCreatedSuccessfully/ZipOperations/IWantToBeZipped2.txt");
        Folder folder1 = openFolder("JFilerCreatedSuccessfully/ZipOperations/IWantToBeZipped1");
        Folder folder2 = openFolder("JFilerCreatedSuccessfully/ZipOperations/IWantToBeZipped2");
        String destination = resource + "/JFilerCreatedSuccessfully/ZipOperations/zipped.zip";

        // When
        jFiler.zip(List.of(file1.getPath(), file2.getPath(), folder1.getPath(), folder2.getPath()), destination);

        // Then
        assertTrue(new File(Paths.get(destination)).exists());
    }

    @Test
    @Order(44)
    public void whenUnZipAZippedFileItMustBeUnZipped() throws Exception {
        // Given
        File zipFile = openFile("JFilerCreatedSuccessfully/ZipOperations/zipped.zip");
        String destination = resource + "/JFilerCreatedSuccessfully/ZipOperations/zipped";

        // When
        jFiler.unzip(zipFile.getPath(), destination);

        // Then
        assertTrue(new File(Paths.get(destination)).exists());

        // After
        jFiler.delete(destination);
    }

    @Test
    @Order(45)
    public void whenUnZipAZippedFileItMustContainsAllOfZippedFiles() throws Exception {
        // Given
        File zipFile = openFile("JFilerCreatedSuccessfully/ZipOperations/zipped.zip");
        String destination = resource + "/JFilerCreatedSuccessfully/ZipOperations/zipped";

        // When
        jFiler.unzip(zipFile.getPath(), destination);

        // Then
        assertEquals(4, openFolder("JFilerCreatedSuccessfully/ZipOperations/zipped").getContains().size());

        // After
        jFiler.delete(destination);
        jFiler.delete(zipFile.getPath());
    }

    @Test
    @Order(46)
    public void whenSearchInFileItMustThrowCannotSearchInFileException() {
        assertThrows(CannotSearchInFileException.class, () -> //
                jFiler.search(".txt$", resource + "/JFilerCreatedSuccessfully/VisibleFile.txt"));
    }

    @Test
    @Order(47)
    public void whenSearchForARegexTheDesiredFilesOrFolderMustBeFound() {
        // Given
        String regex = ".java$";
        String destination = resource + "/JFilerCreatedSuccessfully";
        List<String> desiredFiles = List.of(openFile("JFilerCreatedSuccessfully/InnerFolder/SearchMe.java").getPath()//
                , openFolder("JFilerCreatedSuccessfully/ZipOperations/SearchMe.java").getPath());

        // When
        List<String> foundedFiles = jFiler.search(regex, destination);

        // Then
        assertEquals(desiredFiles, foundedFiles);
    }

    @Test
    @Order(47)
    public void whenCreateANewFileItMustCreateANewFile() throws Exception {
        // Given
        File file = openFile("JFilerCreatedSuccessfully/temp.txt");

        // When
        jFiler.createNewFile(file.getPath());

        // Then
        assertTrue(file.exists());

        // After
        jFiler.delete(file.getPath());
    }

    @Test
    @Order(48)
    public void whenCreateANewFolderItMustCreateANewFolder() throws Exception {
        // Given
        Folder folder = openFolder("JFilerCreatedSuccessfully/temp");

        // When
        jFiler.createNewFolder(folder.getPath());

        // Then
        assertTrue(folder.exists());

        // After
        jFiler.delete(folder.getPath());
    }

    @Test
    @Order(49)
    public void whenDeleteAFileItMustBeDeleted() throws Exception {
        // Given
        File file = openFile("JFilerCreatedSuccessfully/temp.txt");
        jFiler.createNewFile(file.getPath());

        // When
        jFiler.delete(file.getPath());

        // Then
        assertFalse(file.exists());
    }

    @Test
    @Order(50)
    public void whenDeleteAFolderItMustBeDeleted() throws Exception {
        // Given
        Folder folder = openFolder("JFilerCreatedSuccessfully/temp");
        jFiler.createNewFolder(folder.getPath());

        // When
        jFiler.delete(folder.getPath());

        // Then
        assertFalse(folder.exists());
    }

    @Test
    @Order(51)
    public void isFileExistMustReturnTrueIfAFileExists() {
        // Given
        File file = openFile("JFilerCreatedSuccessfully/JFilerCreatedSuccessfully.txt");

        // When
        boolean isFileExists = jFiler.isFileExist(file.getPath());

        assertTrue(isFileExists);
    }

    @Test
    @Order(52)
    public void isFileExistMustReturnFalseIfAFileDoNotExists() {
        // Given
        File file = openFile("JFilerCreatedSuccessfully/temp.txt");

        // When
        boolean isFileExists = jFiler.isFileExist(file.getPath());

        assertFalse(isFileExists);
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
