package playground.imagemarker.io;

import playground.imagemarker.ui.Selection;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * Created by Holger on 19.04.2018.
 */
public class SelectionReader {

    private Path repositoryPath;

    public SelectionReader(Path repositoryPath) {
        this.repositoryPath = repositoryPath;
    }

    public Map<String, Stack<Selection>> readSelections() {
        Map<String, Stack<Selection>> ret = new HashMap<>();
        if (Files.exists(repositoryPath)) {
            try {
                Map<String, List<Selection>> collect = Files.readAllLines(repositoryPath).stream().map(SelectionConverter::fromAnnotation)
                        .collect(Collectors.groupingBy(Selection::getFileName));
                for (String key : collect.keySet()) {
                    Stack<Selection> stack = new Stack<>();
                    collect.get(key).stream().forEach(stack::push);
                    ret.put(key, stack);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }
}
