/**
 *
 */
package de.bsd.x2svg.util;

/**
 * Class with helper utils
 * @author hwr@pilhuhn.de
 *
 */
public class SantasLittleHelper {

    /**
     * Return a file name with appended or changed suffix.
     * @param fileName the original file name
     * @param suffix the suffix *without* the . (i.E. 'png' and not '.png')
     * @return a new file name with the appended suffix
     */
    public static String attachSuffixToFileName(String fileName, String suffix) {
        int pos = fileName.lastIndexOf('.');
        if (pos == -1)
            return fileName + "." + suffix;
        else {
            String baseName = fileName.substring(0, pos);
            return baseName + "." + suffix;
        }
    }

    /**
     * Return the filename part of the passed file system path.
     * @param path Path to a file on the file system.
     * @return the filename part of the passed path.
     */
    public static String getFileNameFromPath(String path) {
        String separator = System.getProperty("file.separator");
        int pos = path.lastIndexOf(separator);
        if (pos == -1)
            return path;
        return path.substring(pos + 1);
    }

    /**
     * Helper method to provide a string with spaces of length howMany
     * @param howMany how long should the string be
     * @return A string with length howMany
     */
    public static String spaces(int howMany) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < howMany; i++) {
            b.append(' ');
        }
        return b.toString();
    }
}
