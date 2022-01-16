package io.github.shuoros.jfiler.util;

import io.github.shuoros.jfiler.JFiler;
import io.github.shuoros.jfiler.file.File;
import io.github.shuoros.jfiler.file.Folder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class JFilerUtils {

    private static void writeFromInputStreamToOutputStream(InputStream is, OutputStream os) throws IOException {
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) > 0) {
            os.write(buffer, 0, length);
        }

        is.close();
        os.close();
    }

    public static void deleteFile(String location) throws IOException {
        File file = File.open(location);
        if (!file.delete())
            throw new IOException(//
                    "Failed to delete the file because: " +//
                            getReasonForFileDeletionFailureInPlainEnglish(file));
    }

    private static String getReasonForFileDeletionFailureInPlainEnglish(File file) {
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

    public static String newNamedLocation(String location, String newName) {
        location = pathSeparatorCorrector(location);
        StringBuilder newNamedLocation = new StringBuilder();
        for (String directory : location.split("/"))
            if (directory.equals(location.split("/")[location.split("/").length - 1]))
                newNamedLocation.append(newName);
            else
                newNamedLocation.append(directory).append("/");

        return newNamedLocation.toString();
    }

    public static void hideFileInUnix(String destination) throws IOException {
        JFiler.rename(destination, "." + destination.split("/")[destination.split("/").length - 1]);
    }

    public static void hideFileInWindows(String destination) throws IOException {
        Files.setAttribute(Paths.get(destination), "dos:hidden", true, LinkOption.NOFOLLOW_LINKS);
    }

    public static void unHideFileInUnix(String destination) throws IOException {
        JFiler.rename(destination, destination.split("/")[destination.split("/").length - 1]//
                .replaceFirst(".", ""));
    }

    public static void unHideFileInWindows(String destination) throws IOException {
        Files.setAttribute(Paths.get(destination), "dos:hidden", false, LinkOption.NOFOLLOW_LINKS);
    }

    public static String pathSeparatorCorrector(String path) {
        return path.replaceAll("\\\\", "/");
    }

    public static void copyFolder(String source, String destination) throws IOException {
        JFiler.createNewFolder(destination);
        for (String file : Objects.requireNonNull(new java.io.File(source).list())) {
            if (new java.io.File(source + "/" + file).isFile())
                copyFile(source + "/" + file, destination + "/" + file);
            else
                copyFolder(source + "/" + file, destination + "/" + file);
        }
    }

    public static void copyFile(String source, String destination) throws IOException {
        JFiler.createNewFile(destination);
        InputStream is = new FileInputStream(source);
        OutputStream os = new FileOutputStream(destination);
        writeFromInputStreamToOutputStream(is, os);
    }

    public static void deleteFolder(String destination) throws IOException {
        Files.walk(Paths.get(destination))
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(java.io.File::delete);
    }

    public static List<String> recursionSearch(String regex, String destination) {
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
