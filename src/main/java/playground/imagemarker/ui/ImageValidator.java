package playground.imagemarker.ui;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Copied from Internet by Holger on 19.04.2018.
 */
public class ImageValidator {
    private static final String IMAGE_PATTERN = "([^\\s]+(\\.(?i)(jpeg|jpg|png|gif|bmp))$)";
    private static Pattern pattern = Pattern.compile(IMAGE_PATTERN);

    public static boolean isImage(String fileName) {
        Matcher matcher = pattern.matcher(fileName);
        return matcher.matches();
    }
}
