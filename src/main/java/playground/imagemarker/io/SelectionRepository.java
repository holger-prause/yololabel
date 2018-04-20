package playground.imagemarker.io;

import javafx.scene.input.MouseEvent;
import playground.imagemarker.ui.Selection;

import java.io.File;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;

/**
 * Created by Holger on 19.04.2018.
 */
public class SelectionRepository {
    private static final String FILE_NAME = "annotations.txt";
    private final Path repositoryPath;
    private Map<String, Stack<Selection>> repository;
    private Stack<Selection> currentSelections = new Stack<>();

    private SelectionReader selectionReader;
    private SelectionWriter selectionWriter;

    public SelectionRepository(File baseDir) {
        repositoryPath = baseDir.toPath().resolve(FILE_NAME);
        selectionReader = new SelectionReader(repositoryPath);
        selectionWriter = new SelectionWriter(repositoryPath);
        repository = Collections.synchronizedMap(selectionReader.readSelections());
    }

    public void addOrPushTop(Selection selection) {
        Stack<Selection> selectionsForFile = repository.get(selection.getFileName());
        if(selectionsForFile == null) {
            selectionsForFile = new Stack<>();
            currentSelections = selectionsForFile;
            repository.put(selection.getFileName(), selectionsForFile);
        }

        if(selectionsForFile.contains(selection)) {
            selectionsForFile.remove(selection);
        }
        selectionsForFile.push(selection);
    }

    public void save() {
        selectionWriter.writeSelections(repository.values());
    }

    public Optional<Selection> getCurrentSelectionFor(MouseEvent event) {
        return currentSelections.stream().filter(e -> e.doSelect(event)).findFirst();
    }

    public Selection getCurrentSelection() {
        return currentSelections.peek();
    }

    public boolean hasCurrentSelection() {
        return !currentSelections.empty();
    }

    public boolean removeLastSelection() {
        if(!currentSelections.isEmpty()) {
            currentSelections.pop();
            return true;
        }
        return false;
    }

    public Stack<Selection> getCurrentSelections() {
        return currentSelections;
    }

    public void setCurrentSelections(String fileName) {
        if(repository.containsKey(fileName)) {
            currentSelections = repository.get(fileName);
        } else {
            currentSelections = new Stack<>();
            repository.put(fileName, currentSelections);
        }
    }
}
