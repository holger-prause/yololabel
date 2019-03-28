package playground.imagemarker.ui.handler;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventType;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import playground.imagemarker.ui.BBox;
import playground.imagemarker.ui.BBoxManager;

/**
 * Created by holger on 23.03.2019.
 */
public class ImageViewManager {
    public static final int BBOX_BORDER_WITH = 1;
    public static final Color BBOX_BORDER_COLOR = Color.DARKBLUE;

    private ActionState currentActionState = ActionState.VIEW_LABELS;
    private Map<ActionState, LabelStateHandler> actionStates;
    private Canvas imageDisplay;
    private ListView<BBox> bboxListView;
    private Image currentImage;
	
    public ImageViewManager(ListView<BBox> bboxListView, Canvas imageDisplay) {
		this.bboxListView = bboxListView;
		this.imageDisplay = imageDisplay;
        actionStates = new HashMap<>();
        actionStates.put(ActionState.VIEW_LABELS, new ViewLabelStateHandler());
        actionStates.put(ActionState.AIM_LABEL, new AimLabelStateHandler());
        actionStates.put(ActionState.DRAW_LABEL, new DrawLabelStateHandler());
        actionStates.put(ActionState.DRAG_LABEL, new DragLabelStateHandler());
        actionStates.put(ActionState.RESIZE_LABEL, new ResizeLabelStateHandler());
    }

    public void handleMouseEvent(MouseEvent mouseEvent) {
        EventType<? extends MouseEvent> eventType = mouseEvent.getEventType();
        LabelStateHandler currentStateHandler = actionStates.get(currentActionState);

        ActionState newState = currentActionState;
        if(eventType == MouseEvent.MOUSE_CLICKED) {
            newState = currentStateHandler.handleMouseClicked(this, mouseEvent);
        }

        if(eventType == MouseEvent.MOUSE_MOVED) {
            newState = currentStateHandler.handleMouseMoved(this, mouseEvent);
        }
        if(newState != null && newState != currentActionState) {
            setNewActionState(newState);
            if(!mouseEvent.isConsumed()) {
                handleMouseEvent(mouseEvent);
            }
        }
    }

    public void handleScrollEvent(ScrollEvent scrollEvent) {
        ActionState newState = currentActionState;
        LabelStateHandler currentStateHandler = actionStates.get(currentActionState);
        newState = currentStateHandler.handleScrollEvent(this, scrollEvent);
        if(newState != null && newState != currentActionState) {
            setNewActionState(newState);
            handleScrollEvent(scrollEvent);
        }
    }

	public void handleKeyEvent(KeyEvent keyEvent) {
		EventType<KeyEvent> eventType = keyEvent.getEventType();
		if (eventType == KeyEvent.KEY_RELEASED) {
			if (!imageDisplay.isDisabled()) {
				switch (keyEvent.getCode()) {
				case F:
					setNewActionState(ActionState.AIM_LABEL);
					break;
				case ESCAPE:
					setNewActionState(ActionState.VIEW_LABELS);
					break;
				case DELETE:
					if (BBoxManager.getInstance().getCurrentDrawingBox() != null) {
						BBoxManager.getInstance().removeCurrentDrawingBox();
						setNewActionState(ActionState.VIEW_LABELS);
					}
					break;
				case A:
					BBoxManager.getInstance().previousImage();
					bindToUI();

					Path currentImagePath = BBoxManager.getInstance().getCurrentEntry().getPath();
					currentImage = new Image(currentImagePath.toUri().toString());
					setNewActionState(ActionState.VIEW_LABELS);
					
					break;
				case D:
					// select rnext image and adopt ui
					BBoxManager.getInstance().nextImage();
					bindToUI();

					currentImagePath = BBoxManager.getInstance().getCurrentEntry().getPath();
					currentImage = new Image(currentImagePath.toUri().toString());
					setNewActionState(ActionState.VIEW_LABELS);
					break;
				default:
					break;
				}
			}
		}
	}

    private void bindToUI() {    	
    	BBoxManager bBoxManager = BBoxManager.getInstance();
		ObservableList<BBox> currentViewBoxes = bBoxManager.getCurrentEntry().getbBoxes();
        bboxListView.setItems(currentViewBoxes);  
        bBoxManager.selectedBBoxProperty().addListener(new ChangeListener<BBox>() {
			@Override
			public void changed(ObservableValue<? extends BBox> observable, BBox oldValue, BBox newValue) {
				if(newValue == null) {
					bboxListView.getSelectionModel().clearSelection();
				} else {
					bboxListView.getSelectionModel().select(newValue);
				}
			}
		});	
           
        bboxListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<BBox>() {
			@Override
			public void changed(ObservableValue<? extends BBox> observable, BBox oldValue, BBox newValue) {
				bBoxManager.startDrawingBox(newValue);
				repaint();
			}
		});
        
        bboxListView.setCellFactory(CheckBoxListCell.forListView(new Callback<BBox, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(BBox bBox) {
            	bBox.visibleProperty().addListener((ChangeListener<Boolean>) 
            			(observable, oldValue, newValue) -> repaint());
                return bBox.visibleProperty();
            }
        }));
    }
    
	public void handleOutsideClicked() {
		BBoxManager.getInstance().endDrawingBox(false);
		if(!imageDisplay.isDisabled())
			repaint();
	}
    
    public void repaint() {
        GraphicsContext graphicsContext = imageDisplay.getGraphicsContext2D();
        graphicsContext.clearRect(0, 0, imageDisplay.getWidth(), imageDisplay.getHeight());
        graphicsContext.drawImage(currentImage, 0, 0);

        for(BBox bBox: BBoxManager.getInstance().getCurrentEntry().getbBoxes()) {
        	paint(bBox, false);
        }

        BBox drawingBox = BBoxManager.getInstance().getCurrentDrawingBox();
        if(drawingBox != null) {
        	paint(drawingBox, true);
        }
    }
    
    private void paint(BBox bBox, boolean selected) {
    	GraphicsContext graphicsContext = imageDisplay.getGraphicsContext2D();
    	if(bBox.visibleProperty().get()) {
        	if(selected) {
                Color selectionFillColor = Color.rgb(4, 80, 204, 0.2);
                graphicsContext.setFill(selectionFillColor);
                graphicsContext.fillRect(bBox.getX(), bBox.getY(),
                		bBox.getW(), bBox.getH());
        	}
        	
            graphicsContext.setLineWidth(BBOX_BORDER_WITH);
            graphicsContext.setStroke(BBOX_BORDER_COLOR);
            graphicsContext.strokeRect(bBox.getX(), bBox.getY(),
            		bBox.getW(), bBox.getH());
    	}
    }

    protected void setNewActionState(ActionState newActionState) {
        System.err.println(String.format("State change from %s to %s", currentActionState, newActionState));
        if(!actionStates.containsKey(newActionState)) {
            throw new RuntimeException(
                    String.format("Invalid state change from %s to %s", currentActionState, newActionState));
        }

        LabelStateHandler currentStateHandler = actionStates.get(currentActionState);
        currentStateHandler.reset();

        LabelStateHandler newActionStateHandler = actionStates.get(newActionState);
        newActionStateHandler.activate(this);
        this.currentActionState = newActionState;
    }

    public GraphicsContext getGraphicsContext() {
        return imageDisplay.getGraphicsContext2D();
    }

    public Canvas getImageDisplay() {
        return imageDisplay;
    }

    public Image getCurrentImage() {
        return currentImage;
    }
}
