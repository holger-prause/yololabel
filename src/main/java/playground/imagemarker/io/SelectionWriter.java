package playground.imagemarker.io;

import playground.imagemarker.ui.Selection;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Stack;

/**
 * Created by Holger on 19.04.2018.
 */
public class SelectionWriter {
    private Path repositoryPath;

    public SelectionWriter(Path repositoryPath) {
        this.repositoryPath = repositoryPath;
    }

    public void writeSelections(Collection<Stack<Selection>> selections) {
        try (final PrintWriter writer = new PrintWriter(repositoryPath.toFile())) {
            selections.stream().forEach(e ->
                    e.forEach(a -> writer.println(SelectionConverter.convertToAnnotation(a))));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
