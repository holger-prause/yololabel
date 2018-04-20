package playground.imagemarker.io;

import javafx.scene.shape.Rectangle;
import playground.imagemarker.ui.Selection;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Holger on 19.04.2018.
 */
public class SelectionConverter {

    private static final Pattern linePattern = Pattern.compile("([ \\:\\w\\/\\-\\\\\\.]+) ([0-9]+) ([0-9]+) ([0-9]+) ([0-9]+) ([0-9]+)");

    public static String convertToAnnotation(Selection selection) {
        //String filePart = picture.getParent() + File.separator + picture.getName();
        Rectangle rect = selection.getRectangle();
        String line = "%s 1 %s %s %s %s";
        return String.format(line, selection.getFileName(), (int) rect.getX(), (int) rect.getY(), (int) rect.getWidth(), (int) rect.getHeight());
    }

    public static Selection fromAnnotation(String line) {
        Matcher lineMatcher = linePattern.matcher(line);
        lineMatcher.find();
        String fileName = lineMatcher.group(1);
        double x = Double.parseDouble(lineMatcher.group(3));
        double y = Double.parseDouble(lineMatcher.group(4));
        double width = Double.parseDouble(lineMatcher.group(5));
        double height = Double.parseDouble(lineMatcher.group(6));
        return new Selection(fileName, x, y, width, height);
    }
}
