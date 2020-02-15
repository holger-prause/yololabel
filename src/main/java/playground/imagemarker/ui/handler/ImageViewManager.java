package playground.imagemarker.ui.handler;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javafx.application.Platform;
import javafx.event.EventType;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import playground.imagemarker.io.IFileWatchNotifieable;
import playground.imagemarker.ui.BBox;
import playground.imagemarker.ui.BBoxManager;
import playground.imagemarker.ui.LabelsManager;
import playground.imagemarker.ui.PickLabelDialog;


/**
 * Created by holger on 23.03.2019.
 */
public class ImageViewManager implements IFileWatchNotifieable {


    public static final int BBOX_BORDER_WITH = 2;
    public static final Color BBOX_BORDER_COLOR = Color.WHITE;
    private ActionState currentActionState = ActionState.VIEW_LABELS;
    private Map<ActionState, UIStateHandler> actionStates;
    private Canvas imageDisplay;
    private ListView<BBox> bboxListView;
    private TextField imageNameLabel;
    private ScrollPane scrollPane;

    public ImageViewManager(TextField imageNameLabel, ScrollPane scrollPane, ListView<BBox> bboxListView, Canvas imageDisplay) {
        this.imageNameLabel = imageNameLabel;
        this.scrollPane = scrollPane;
		this.bboxListView = bboxListView;
		this.imageDisplay = imageDisplay;
        actionStates = new HashMap<>();
        actionStates.put(ActionState.VIEW_LABELS, new ViewLabelStateHandler());
        actionStates.put(ActionState.AIM_LABEL, new AimLabelStateHandler());
        actionStates.put(ActionState.DRAW_LABEL, new DrawLabelStateHandler());
        actionStates.put(ActionState.DRAG_LABEL, new DragLabelStateHandler());
        actionStates.put(ActionState.RESIZE_LABEL, new ResizeLabelStateHandler());
        actionStates.put(ActionState.MOVE_IMAGE, new MoveImageHandler());
        actionStates.put(ActionState.GOTO_IMAGE, new GotoImageHandler());
    }

    public void handleMouseEvent(MouseEvent mouseEvent) {
        EventType<? extends MouseEvent> eventType = mouseEvent.getEventType();
        UIStateHandler currentStateHandler = actionStates.get(currentActionState);

        ActionState newState = currentActionState;
        if(eventType == MouseEvent.MOUSE_CLICKED) {
            newState = currentStateHandler.handleMouseClicked(this, mouseEvent);
        }

        if(eventType == MouseEvent.MOUSE_MOVED) {
            newState = currentStateHandler.handleMouseMoved(this, mouseEvent);
        }
        if(newState != null && newState != currentActionState) {
            setNewActionState(newState, mouseEvent);
        }
    }

    public void handleScrollEvent(ScrollEvent scrollEvent) {
        UIStateHandler currentStateHandler = actionStates.get(currentActionState);
        ActionState newState = currentStateHandler.handleScrollEvent(this, scrollEvent);
        if(newState != null && newState != currentActionState) {
            setNewActionState(newState, scrollEvent);
            handleScrollEvent(scrollEvent);
        }
    }

    public void handleImageDirSelected(Path imgDir) {
        BBoxManager boxManager = BBoxManager.getInstance();
        boxManager.imageDirectorySelected(imgDir, this);
        if(boxManager.hasEntries()) {
            nextImage();
            imageDisplay.setDisable(false);
        }
        setNewActionState(ActionState.VIEW_LABELS, null);
    }

    @Override
    public void fileCreated(Path file) {
        //from file watch thread
        Platform.runLater(() -> {
            imageDisplay.setDisable(false);
            setNewActionState(ActionState.VIEW_LABELS, null);
        });
    }

    public void handleKeyEvent(KeyEvent keyEvent) {
        if (imageDisplay.isDisabled()) {
            return;
        }

		EventType<KeyEvent> eventType = keyEvent.getEventType();
        BBoxManager boxManager = BBoxManager.getInstance();

        if(eventType == KeyEvent.KEY_PRESSED) {
            switch (keyEvent.getCode()) {
                case A:
                    prevImage();
                    break;
                case D:
                    nextImage();
                    break;
                case H:
                    BBoxManager.getInstance().listToIndex();
                    break;
				default:
					break;
            }
        }
        else if (eventType == KeyEvent.KEY_RELEASED) {
			if (!imageDisplay.isDisabled()) {
                BBox currentDrawingBox = boxManager.getCurrentDrawingBox();
                switch (keyEvent.getCode()) {
				case W:
					setNewActionState(ActionState.AIM_LABEL, keyEvent);
					break;
				case ESCAPE:
					setNewActionState(ActionState.VIEW_LABELS, keyEvent);
					break;
				case DELETE:
					if (currentDrawingBox != null) {
						boxManager.removeCurrentDrawingBox();
						setNewActionState(ActionState.VIEW_LABELS, keyEvent);
					}
					break;
                case M:
                    setNewActionState(ActionState.MOVE_IMAGE, keyEvent);
                    setNewActionState(ActionState.VIEW_LABELS, keyEvent);
                    break;
                case G:
                    setNewActionState(ActionState.GOTO_IMAGE, keyEvent);
                    setNewActionState(ActionState.VIEW_LABELS, keyEvent);
                    break;
                case C:
                    if (currentDrawingBox != null) {
                        PickLabelDialog pickLabelDialog = new PickLabelDialog();
                        boolean success = pickLabelDialog.show(PickLabelDialog.DialogType.COPY);
                        if(success) {
                            BBox copy = new BBox(currentDrawingBox);
                            copy.setLabel(LabelsManager.getInstance().getLastSelectedLabel());
                            boxManager.addBBox(copy);
                        }
                        BBoxManager.getInstance().endDrawingBox(false);
                        setNewActionState(ActionState.VIEW_LABELS, keyEvent);
                    }
                    break;
                case R:
                    if (currentDrawingBox != null) {
                        PickLabelDialog pickLabelDialog = new PickLabelDialog();
                        boolean success = pickLabelDialog.show(PickLabelDialog.DialogType.RENAME);
                        if(success) {
                            currentDrawingBox.setLabel(LabelsManager.getInstance().getLastSelectedLabel());
                        }
                        BBoxManager.getInstance().endDrawingBox(success);
                        setNewActionState(ActionState.VIEW_LABELS, keyEvent);
                    }
                    break;

                case S:
                    if (currentDrawingBox != null) {
                        boxManager.removeCurrentDrawingBox();
                        setNewActionState(ActionState.VIEW_LABELS, keyEvent);
                    }
                    break;
				default:
					break;
				}
			}
		}
	}

    public void handleOutsideClicked(MouseEvent event) {
        if(!imageDisplay.isDisabled()) {
            BBoxManager.getInstance().endDrawingBox(false);
            repaint();
        }
    }

    public void repaint() {
        GraphicsContext graphicsContext = imageDisplay.getGraphicsContext2D();
        graphicsContext.clearRect(0, 0, imageDisplay.getWidth(), imageDisplay.getHeight());
        graphicsContext.drawImage(BBoxManager.getInstance().getCurrentImage(), 0, 0);

        Set<BBox> drawingBoxes = new HashSet<>();
        drawingBoxes.addAll(BBoxManager.getInstance().getCurrentEntry().getbBoxes());
        BBox currentDrawingBox = BBoxManager.getInstance().getCurrentDrawingBox();
        if(currentDrawingBox != null) {
            drawingBoxes.add(currentDrawingBox);
        }

        for(BBox bBox: drawingBoxes) {
            boolean selected = bBox == currentDrawingBox;
            repaint(bBox, selected);
        }
    }

    private void setNewActionState(ActionState newActionState, InputEvent inputEvent) {
        if(!actionStates.containsKey(newActionState)) {
            throw new RuntimeException(
                    String.format("Invalid state change from %s to %s", currentActionState, newActionState));
        }
        //System.err.println("switching from " + currentActionState.name() + "to: "+newActionState.name());

        UIStateHandler currentStateHandler = actionStates.get(currentActionState);
        currentStateHandler.reset();

        UIStateHandler newActionStateHandler = actionStates.get(newActionState);
        newActionStateHandler.activate(this, inputEvent);
        this.currentActionState = newActionState;
    }

    private void nextImage() {
        BBoxManager boxManager = BBoxManager.getInstance();
        boxManager.nextImage();
        setNewActionState(ActionState.VIEW_LABELS, null);
    }

    private void prevImage() {
        BBoxManager boxManager = BBoxManager.getInstance();
        boxManager.previousImage();
        setNewActionState(ActionState.VIEW_LABELS, null);
    }

    private void repaint(BBox bBox, boolean selected) {
        GraphicsContext graphicsContext = imageDisplay.getGraphicsContext2D();
    	if(bBox.visibleProperty().get()) {
        	if(selected) {
                Color selectionFillColor = Color.rgb(4, 80, 204, 0.6);
                graphicsContext.setFill(selectionFillColor);
                graphicsContext.fillRect(bBox.getX(), bBox.getY(),
                		bBox.getW(), bBox.getH());
                graphicsContext.setStroke(BBOX_BORDER_COLOR);
        	} else {
                graphicsContext.setStroke(Color.rgb(178,34,34, 0.7));
            }
        	
            graphicsContext.setLineWidth(BBOX_BORDER_WITH);
            graphicsContext.strokeRect(bBox.getX(), bBox.getY(),
            		bBox.getW(), bBox.getH());
    	}
    }

    public Canvas getImageDisplay() {
        return imageDisplay;
    }

	public ScrollPane getScrollPane() {
		return scrollPane;
	}

    public ListView<BBox> getBboxListView() {
        return bboxListView;
    }

    public TextField getImageNameLabel() {
        return imageNameLabel;
    }
}
