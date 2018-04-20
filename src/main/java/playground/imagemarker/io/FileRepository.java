package playground.imagemarker.io;

import playground.imagemarker.ui.ImageValidator;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Created by Holger on 20.04.2018.
 */
public class FileRepository {

    private final List<File> files;
    private int index = -1;

    public FileRepository(File baseDir) {
        File[] filteredFiles = baseDir.listFiles((dir, name) -> {
            return ImageValidator.isImage(name);
        });
        files = Arrays.asList(filteredFiles);
    }

    public boolean hasFiles() {
        return !files.isEmpty();
    }

    public Optional<File> nextFile() {
        if (index + 1 <= files.size() - 1) {
            index++;
            return Optional.of(files.get(index));

        }
        return Optional.empty();
    }

    public Optional<File> prevFile() {
        if (index - 1 >= 0) {
            index--;
            return Optional.of(files.get(index));
        }

        return Optional.empty();
    }

    public Optional<File> currentFile() {
        if (index >= 0 && index <= files.size() - 1) {
            return Optional.of(files.get(index));
        }
        return Optional.empty();
    }

    public int size() {
        return files.size();
    }

    public int currentIndex() {
        return index;
    }
}
