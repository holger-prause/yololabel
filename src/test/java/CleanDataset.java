import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by holger on 30.03.2019.
 */
public class CleanDataset {
    public static void main(String[] args) throws IOException {

        Path path = Paths.get("C:\\development\\dataset\\open_images_lp_test\\img\\positives");
        List<Path> annotations = Files.list(path).filter(e -> e.getFileName().toString().endsWith(".txt")).collect(Collectors.toList());

        for(Path annotationFile: annotations) {
            List<String> lines = Files.readAllLines(annotationFile);
            List<String> filteredLines = lines.stream().map(CleanDataset::mergeClass)
                    .filter(e -> e != null).collect(Collectors.toList());

            Files.write(annotationFile, filteredLines, Charset.forName("UTF-8"));
        }
    }

    private static String mergeClass(String line) {
        String[] split = line.split(" ");
        int clsIdx = Integer.parseInt(split[0]);
        if(clsIdx > 2 && clsIdx < 6) {
            clsIdx = 2;
        }
        split[0] = String.valueOf(clsIdx);
        return String.join(" ", split);
    }
}
