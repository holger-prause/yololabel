import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by holger on 24.04.2019.
 */
public class ValidateTest {
    public static void main(String[] args) throws IOException {


        Path targetDir = Paths.get("C:\\development\\dataset\\open_images_lp_train_full\\img\\positives");
        List<String> targetAnnotations = Files.list(targetDir)
                .filter(e -> e.getFileName().toString().endsWith(".jpg"))
                .map((e) -> {
                    String fName = e.getFileName().toString();
                    int i = fName.indexOf(".");
                    return fName.substring(0, i);
                })
                .collect(Collectors.toList());
        List<String> targetImages = Files.list(targetDir)
                .filter(e -> e.getFileName().toString().endsWith(".txt"))
                .map((e) -> {
                    String fName = e.getFileName().toString();
                    int i = fName.indexOf(".");
                    return fName.substring(0, i);
                })
                .collect(Collectors.toList());

        if(targetAnnotations.size() != targetImages.size()) {
            if(targetAnnotations.size() > targetImages.size()) {
                List<String> diff = getDiff(targetAnnotations, targetImages);
                System.err.println("annotation with no images:" + String.join("\n", diff));
            } else {
                List<String> diff = getDiff(targetImages, targetAnnotations);
                System.err.println("images with no annotation:" + String.join("\n", diff));
            }
        }
    }

    private static List<String> getDiff(List<String> src, List<String> target) {
        return src.stream().filter(e -> !target.contains(e)).collect(Collectors.toList());
    }
}
