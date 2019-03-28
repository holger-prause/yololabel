package playground.imagemarker.ui.handler;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.stage.Stage;
import playground.imagemarker.ui.BBox;
import playground.imagemarker.ui.BBoxManager;
import playground.imagemarker.ui.StageManager;
import playground.imagemarker.util.BBoxUtil;
import playground.imagemarker.util.BBoxUtil.CornerType;

import java.nio.file.Path;
import java.util.List;

/**
 * Created by holger on 23.03.2019.
 */
public class ViewLabelStateHandler extends LabelStateHandler {
    private final String BASE_TITLE = "Image Marker";
    private final double INITIAL_SCALE = 1.0;
    private final double SCALE_DELTA_FACTOR = 0.3;
    private DoubleProperty scaleProperty = new SimpleDoubleProperty(INITIAL_SCALE);

    private Path imPath;
    public ViewLabelStateHandler() {
        super(ActionState.VIEW_LABELS);
    }

    @Override
    public void activate(ImageViewManager manager) {
        Image currentImage = BBoxManager.getInstance().getCurrentImage();
        Canvas imageDisplay = manager.getImageDisplay();
        Path currentImagePath = BBoxManager.getInstance().getCurrentEntry().getPath();
        //only do once per image
        if(imPath == null || !imPath.equals(currentImagePath)) {
            imPath = currentImagePath;

            scaleProperty.setValue(INITIAL_SCALE);
            imageDisplay.scaleXProperty().bind(scaleProperty);
            imageDisplay.scaleYProperty().bind(scaleProperty);
            imageDisplay.setWidth(currentImage.getWidth());
            imageDisplay.setHeight(currentImage.getHeight());


            BBoxManager boxManager = BBoxManager.getInstance();
            Stage stage = (Stage) imageDisplay.getScene().getWindow();
            String title = "%s      %-35s Resolution %s x %s      File %s of %s";
            String format = String.format(title, BASE_TITLE, currentImagePath.getFileName().toString()
                    , (int)currentImage.getWidth(), (int)currentImage.getHeight(),
                    boxManager.getCurrentIndex()+1, boxManager.getRepositorySize());
            stage.setTitle(format);
        }
        
        manager.repaint();
    }

    @Override
    public ActionState handleScrollEvent(ImageViewManager manager, ScrollEvent scrollEvent) {
        boolean zoomIn = scrollEvent.getDeltaY() > 0;
        if (zoomIn) {
            scaleProperty.setValue(scaleProperty.get() + SCALE_DELTA_FACTOR);
        } else {
            scaleProperty.setValue(scaleProperty.get() - SCALE_DELTA_FACTOR);
            if (scaleProperty.get() < INITIAL_SCALE) {
                scaleProperty.setValue(INITIAL_SCALE);
            }
        }
        return getActionState();
    }

    @Override
    public ActionState handleMouseMoved(ImageViewManager manager, MouseEvent mouseEvent) {
        List<BBox> currentViewBoxes = BBoxManager.getInstance().getCurrentEntry().getbBoxes();
        CornerType resizeCorner = null;
        Cursor cursor = Cursor.DEFAULT;
        for(BBox bBox: currentViewBoxes) {
        	resizeCorner = BBoxUtil.getResizeCorner(bBox, mouseEvent);
            if (resizeCorner != null) {
            	if(resizeCorner == CornerType.TOP_LEFT || resizeCorner == CornerType.BOTTOM_RIGHT) {
                	cursor = Cursor.SE_RESIZE;
                } else {
                	cursor = Cursor.SW_RESIZE;
                }
                break;
            }
            
            if(BBoxUtil.isWithinBBox(bBox, mouseEvent)) {
        		cursor = Cursor.CLOSED_HAND;
        		break;
        	}
        }
        
        
        StageManager stageManager = StageManager.getInstance();
        stageManager.getScene().setCursor(cursor);
        return getActionState();
    }

    @Override
    public ActionState handleMouseClicked(ImageViewManager manager, MouseEvent mouseEvent) {
        ActionState returnActionState = getActionState();
        List<BBox> currentViewBoxes = BBoxManager.getInstance().getCurrentEntry().getbBoxes();
        
        for(BBox bBox: currentViewBoxes) {
            BBoxUtil.CornerType focusCorner = BBoxUtil.getResizeCorner(bBox, mouseEvent);
            if (focusCorner != null) {
                BBoxManager.getInstance().startDrawingBox(bBox);
                returnActionState = ActionState.RESIZE_LABEL;
                break;
            }
            
            if(BBoxUtil.isWithinBBox(bBox, mouseEvent)) {
            	BBoxManager.getInstance().startDrawingBox(bBox);
            	returnActionState = ActionState.DRAG_LABEL;
            	break;
        	}
        }

        //paint selection to give feedback about selected box
        if(returnActionState == getActionState()) {
        	BBoxManager.getInstance().endDrawingBox(false);
        } 
        manager.repaint();
        return returnActionState;
    }

    @Override
    public void reset() {

    }

}
