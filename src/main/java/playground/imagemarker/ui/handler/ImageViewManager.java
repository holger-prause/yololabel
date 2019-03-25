package playground.imagemarker.ui.handler;

import javafx.event.EventType;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import playground.imagemarker.ui.BBox;
import playground.imagemarker.ui.BBoxManager;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by holger on 23.03.2019.
 */
public class ImageViewManager {
    public static final int BBOX_BORDER_WITH = 2;
    public static final Color BBOX_BORDER_COLOR = Color.DARKBLUE;

    private ActionState currentActionState = ActionState.VIEW_LABELS;
    private Map<ActionState, LabelStateHandler> actionStates;
    private Canvas imageDisplay;
    private Image currentImage;

    public ImageViewManager(Canvas imageDisplay) {
        this.imageDisplay = imageDisplay;
        actionStates = new HashMap<>();
        actionStates.put(ActionState.VIEW_LABELS, new ViewLabelStateHandler());
        actionStates.put(ActionState.AIM_LABEL, new AimLabelStateHandler());
        actionStates.put(ActionState.DRAW_LABEL, new DrawLabelStateHandler());
        actionStates.put(ActionState.DRAG_LABEL, new DragLabelStateHandler());
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
        if(eventType == KeyEvent.KEY_RELEASED) {
            if(keyEvent.getCode() == KeyCode.F) {
                setNewActionState(ActionState.AIM_LABEL);
            }

            if(keyEvent.getCode() == KeyCode.ESCAPE) {
                setNewActionState(ActionState.VIEW_LABELS);
            }
        }
    }

    public void imageSelected(Path imagePath) {
        BBoxManager.getInstance().imageSelected(imagePath);
        currentImage = new Image(imagePath.toUri().toString());
        setNewActionState(ActionState.VIEW_LABELS);
    }

    protected void repaint() {
        GraphicsContext graphicsContext = imageDisplay.getGraphicsContext2D();
        graphicsContext.clearRect(0, 0, imageDisplay.getWidth(), imageDisplay.getHeight());
        graphicsContext.drawImage(currentImage, 0, 0);

        BBox drawingBox = BBoxManager.getInstance().getCurrentDrawingBox();
        for(BBox bBox: BBoxManager.getInstance().getCurrentViewBoxes()) {
            graphicsContext.setLineWidth(BBOX_BORDER_WITH);
            graphicsContext.setStroke(BBOX_BORDER_COLOR);
            graphicsContext.strokeRect(bBox.getX(), bBox.getY(),
                    bBox.getW(), bBox.getH());
        }

        if(drawingBox != null) {
            graphicsContext.setLineWidth(BBOX_BORDER_WITH);
            graphicsContext.setStroke(BBOX_BORDER_COLOR);
            graphicsContext.strokeRect(drawingBox.getX(), drawingBox.getY(),
                    drawingBox.getW(), drawingBox.getH());
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
