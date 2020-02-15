import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by holger on 30.03.2019.
 */
public class MergeDataset {
    public static void main(String[] args) throws IOException {
        Path srcDir = Paths.get("C:\\development\\dataset\\open_images_lp_validation_patched\\img\\positives");
        List<Path> srcAnnotations = Files.list(srcDir)
                .filter(e -> e.getFileName().toString().endsWith(".txt")).collect(Collectors.toList());

        Path targetDir = Paths.get("C:\\development\\dataset\\open_images_lp_validation_full\\img\\positives");
        List<Path> targetAnnotations = Files.list(targetDir)
                .filter(e -> e.getFileName().toString().endsWith(".txt")).collect(Collectors.toList());


        for(Path srcAnnotation: srcAnnotations) {
            Optional<Path> targetAnnotation = getTargetAnnotation(targetAnnotations, srcAnnotation);
            if(targetAnnotation.isPresent()) {
                List<String> srcLines = Files.readAllLines(srcAnnotation);
                List<String> targetLines = Files.readAllLines(targetAnnotation.get());


                List<String> mergedLines = new ArrayList<>();
                mergedLines.addAll(targetLines);
                mergedLines.addAll(srcLines);

                Files.write(targetAnnotation.get(), mergedLines, Charset.forName("UTF-8"));
            }
        }
    }

    private static Optional<Path> getTargetAnnotation(List<Path> annotations, Path annotation) {
        Optional<Path> first = annotations.stream()
                .filter(e -> e.getFileName().equals(annotation.getFileName()))
                .findFirst();
        return first;
    }


}
