package com.codicefun.tc.soa.util;

/**
 * Utility class for system-related operations
 */
public class SystemUtil {

    /**
     * Operating system name(lower case)
     */
    private static final String OS_NAME = System.getProperty("os.name").toLowerCase();

    /**
     * Check if the system is Windows
     *
     * @return true if the system is Windows, false otherwise
     */
    public static boolean isWindows() {
        return OS_NAME.contains("win");
    }

    /**
     * Check if the system is Linux
     *
     * @return true if the system is Linux, false otherwise
     */
    public static boolean isLinux() {
        return OS_NAME.contains("nix") || OS_NAME.contains("nux");
    }

    /**
     * Check if the system is macOS
     *
     * @return true if the system is macOS, false otherwise
     */
    public static boolean isMac() {
        return OS_NAME.contains("mac");
    }

    /**
     * Check if the system is Unix
     *
     * @return true if the system is Unix, false otherwise
     */
    public static boolean isUnix() {
        return isLinux() || isMac();
    }

}
