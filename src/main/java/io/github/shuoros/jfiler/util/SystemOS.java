package io.github.shuoros.jfiler.util;

/**
 * A utility class to extract type of OS running in machine.
 *
 * @author Soroush Shemshadi
 * @version 1.0.0
 * @see <a href="https://github.com/shuoros/JFiler">JFiler</a>
 * @since 1.0.0
 */
public class SystemOS {

    private static String OS = System.getProperty("os.name").toLowerCase();

    /**
     * Check if OS is windows or not
     *
     * @return True if system's os is windows and false if not.
     */
    public static boolean isWindows() {
        return (OS.contains("win"));
    }

    /**
     * Check if OS is mac or not
     *
     * @return True if system's os is mac and false if not.
     */
    public static boolean isMac() {
        return (OS.contains("mac"));
    }

    /**
     * Check if OS is unix based or not
     *
     * @return True if system's os is unix based and false if not.
     */
    public static boolean isUnix() {
        return (OS.contains("nix")
                || OS.contains("nux")
                || OS.contains("aix"));
    }

    /**
     * Check if OS is solaris or not
     *
     * @return True if system's os is solaris and false if not.
     */
    public static boolean isSolaris() {
        return (OS.contains("sunos"));
    }

}
