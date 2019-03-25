package playground.imagemarker.ui;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by holger on 24.03.2019.
 */
public class BBoxManager {

    private static BBoxManager instance;
    private Map<Path, List<BBox>> boxes = new HashMap<>();
    private List<BBox> currentViewBoxes = new ArrayList<>();
    private Path currentImagePath;
    private BBox currentDrawingBox;

    private BBoxManager() {
    }

    public static BBoxManager getInstance() {
        if(instance == null) {
            instance = new BBoxManager();
        }
        return instance;
    }

    public void imageSelected(Path imgPath) {
        currentImagePath = imgPath;
        currentViewBoxes = new ArrayList<>();
        boxes.put(imgPath.toAbsolutePath(), currentViewBoxes);
    }

    public void endDrawingBox(boolean success) {
        if(success) {
            currentViewBoxes.add(currentDrawingBox);
        }
        currentDrawingBox = null;
    }

    public Path getCurrentImagePath() {
        return currentImagePath;
    }

    public BBox getCurrentDrawingBox() {
        return currentDrawingBox;
    }

    public void startDrawingBox(BBox currentDrawingBox) {
        this.currentDrawingBox = currentDrawingBox;
    }

    public List<BBox> getCurrentViewBoxes() {
        return currentViewBoxes;
    }

    public void setCurrentViewBoxes(List<BBox> currentViewBoxes) {
        this.currentViewBoxes = currentViewBoxes;
    }
}
