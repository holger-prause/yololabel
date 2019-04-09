package playground.imagemarker.ui.handler;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.stage.Stage;
import javafx.util.Callback;
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
public class ViewLabelStateHandler extends UIStateHandler {
    public static final String BASE_TITLE = "Yolo Label";    
    private final double INITIAL_SCALE = 1.0;
    private final double SCALE_DELTA_FACTOR = 0.3;
    private DoubleProperty scaleProperty = new SimpleDoubleProperty(INITIAL_SCALE);

    private Path imPath;
    public ViewLabelStateHandler() {
        super(ActionState.VIEW_LABELS);
    }

    @Override
    public void activate(ImageViewManager manager) {
        BBoxManager boxManager = BBoxManager.getInstance();
        int repositorySize = boxManager.getRepositorySize();
        if(repositorySize == 0) {
            disableImageDisplay(manager.getImageDisplay());
            StageManager stageManager = StageManager.getInstance();
            stageManager.getScene().setCursor(Cursor.DEFAULT);

            manager.getBboxListView().getItems().clear();
            Stage stage = StageManager.getInstance().getPrimaryStage();
            stage.setTitle(BASE_TITLE);

            manager.getImageNameLabel().setText("");
            return;
        }

        Image currentImage = boxManager.getCurrentImage();
        Canvas imageDisplay = manager.getImageDisplay();
        Path currentImagePath = boxManager.getCurrentEntry().getPath();
        //only do once per image
        if(imPath == null || !imPath.equals(currentImagePath)) {
            imPath = currentImagePath;
            manager.getImageNameLabel().setText(imPath.getFileName().toString());

            scaleProperty.setValue(INITIAL_SCALE);
            imageDisplay.scaleXProperty().bind(scaleProperty);
            imageDisplay.scaleYProperty().bind(scaleProperty);
            imageDisplay.setWidth(currentImage.getWidth());
            imageDisplay.setHeight(currentImage.getHeight());

            Stage stage = (Stage) imageDisplay.getScene().getWindow();
            String title = "%s      %-35s Resolution %s x %s      File %s of %s";
            String format = String.format(title, BASE_TITLE, currentImagePath.getFileName().toString()
                    , (int)currentImage.getWidth(), (int)currentImage.getHeight(),
                    boxManager.getCurrentIndex()+1, boxManager.getRepositorySize());
            stage.setTitle(format);

            ObservableList<BBox> currentViewBoxes = boxManager.getCurrentEntry().getbBoxes();
            ListView<BBox> bboxListView = manager.getBboxListView();
            bboxListView.setItems(currentViewBoxes);
            boxManager.selectedBBoxProperty().addListener((observable, oldValue, newValue) -> {
                if(newValue == null) {
                    bboxListView.getSelectionModel().clearSelection();
                } else {
                    bboxListView.getSelectionModel().select(newValue);
                }
            });

            bboxListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if(newValue != null) {
                    boxManager.startDrawingBox(newValue);
                    manager.repaint();
                }
            });

            bboxListView.setCellFactory(CheckBoxListCell.forListView(new Callback<BBox, ObservableValue<Boolean>>() {
                @Override
                public ObservableValue<Boolean> call(BBox bBox) {
                    bBox.visibleProperty().addListener((observable, oldValue, newValue) -> manager.repaint());
                    return bBox.visibleProperty();
                }
            }));
        }

        manager.repaint();
    }

    @Override
    public ActionState handleScrollEvent(ImageViewManager manager, ScrollEvent scrollEvent) {
    	ScrollPane scrollPane = manager.getScrollPane();
    	Canvas imageDisplay = manager.getImageDisplay();
    	
        boolean zoomIn = scrollEvent.getDeltaY() > 0;
        if (zoomIn) {
            scaleProperty.setValue(scaleProperty.get() + SCALE_DELTA_FACTOR);
        } else {
            scaleProperty.setValue(scaleProperty.get() - SCALE_DELTA_FACTOR);
            if (scaleProperty.get() < INITIAL_SCALE) {
                scaleProperty.setValue(INITIAL_SCALE);
            }
        }
        
        double newW = manager.getImageDisplay().getWidth() * scaleProperty.get();        
        double newH = manager.getImageDisplay().getHeight() * scaleProperty.get();
        if(newW > scrollPane.getWidth()) {
        	double hValue = scrollEvent.getX() / imageDisplay.getWidth();
        	scrollPane.setHvalue(hValue);
        }
        
        if(newH > scrollPane.getHeight()) {
        	double vValue = scrollEvent.getY() / imageDisplay.getHeight();
        	scrollPane.setVvalue(vValue);
        }
        
        return getActionState();
    }

    @Override
    public ActionState handleMouseMoved(ImageViewManager manager, MouseEvent mouseEvent) {
        List<BBox> currentViewBoxes = BBoxManager.getInstance().getCurrentEntry().getbBoxes();
        Cursor cursor = Cursor.DEFAULT;
    	BBox focusBox = BBoxUtil.findFocusBBox(currentViewBoxes, mouseEvent);
    	if(focusBox != null) {
    		BBoxUtil.CornerType resizeCorner = BBoxUtil.getResizeCorner(focusBox, mouseEvent);
            if (resizeCorner != null) {
            	if(resizeCorner == CornerType.TOP_LEFT || resizeCorner == CornerType.BOTTOM_RIGHT) {
                	cursor = Cursor.SE_RESIZE;
                } else {
                	cursor = Cursor.SW_RESIZE;
                }
            } else {
            	cursor = Cursor.CLOSED_HAND;
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
        BBox focusBox = null;
        
        focusBox = BBoxUtil.findFocusBBox(currentViewBoxes, mouseEvent);
        if(focusBox == null) {
        	focusBox = BBoxManager.getInstance().getCurrentDrawingBox();
        }
                
    	if(focusBox != null) {
    		BBoxUtil.CornerType resizeCorner = BBoxUtil.getResizeCorner(focusBox, mouseEvent);
            if (resizeCorner != null) {
                returnActionState = ActionState.RESIZE_LABEL;
            } else {
            	returnActionState = ActionState.DRAG_LABEL;
            }
            
        	BBoxManager.getInstance().startDrawingBox(focusBox);
    	}

        //paint selection to give feedback about selected box
        if(returnActionState == getActionState()) {
        	BBoxManager.getInstance().endDrawingBox(false);
        } 
        manager.repaint();
        return returnActionState;
    }

    public void disableImageDisplay(Canvas imageDisplay) {
        GraphicsContext graphicsContext = imageDisplay.getGraphicsContext2D();
        graphicsContext.clearRect(0, 0, imageDisplay.getWidth(), imageDisplay.getHeight());
        imageDisplay.setDisable(true);
    }


    @Override
    public void reset() {

    }
    
}
