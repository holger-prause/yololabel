package playground.imagemarker.ui.handler;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import playground.imagemarker.ui.BBox;
import playground.imagemarker.ui.BBoxManager;
import playground.imagemarker.ui.PickLabelDialog;
import playground.imagemarker.ui.StageManager;

public class DragLabelStateHandler extends LabelStateHandler{
	private Point2D initialDragPoint;
	private Point2D initialBBoxOrigin;
	private BBox bBox; 
	
	public DragLabelStateHandler() {
		super(ActionState.DRAG_LABEL);
	}

	@Override
	public void activate(ImageViewManager manager) {
		
	}

	@Override
	public ActionState handleMouseMoved(ImageViewManager manager, MouseEvent mouseEvent) {
		double newX;
		double newY;
		double distX = Math.abs(initialDragPoint.getX() - mouseEvent.getX());
		double distY = Math.abs(initialDragPoint.getY() - mouseEvent.getY());
		
		if(mouseEvent.getX() < initialDragPoint.getX()) {
			newX  = initialBBoxOrigin.getX() - distX;
		} else {
			newX  = initialBBoxOrigin.getX() + distX;
		}
		
		if(mouseEvent.getY() < initialDragPoint.getY()) {
			newY = initialBBoxOrigin.getY() - distY;
		} else {
			newY = initialBBoxOrigin.getY() + distY;
		}
		
		bBox.setX(newX);
		bBox.setY(newY);		
		manager.repaint();
		// TODO Auto-generated method stub
		return getActionState();
	}

	@Override
	public ActionState handleMouseClicked(ImageViewManager manager, MouseEvent mouseEvent) {
		ActionState actionState = getActionState();
		if(bBox == null) {
			bBox = BBoxManager.getInstance().getCurrentDrawingBox();
			initialDragPoint = new Point2D(mouseEvent.getX(), mouseEvent.getY());
			initialBBoxOrigin = new Point2D(bBox.getX(), bBox.getY());
		} else {
			actionState = ActionState.VIEW_LABELS;
			mouseEvent.consume();
		}
		
		return actionState;
	}

	@Override
	public ActionState handleScrollEvent(ImageViewManager manager, ScrollEvent scrollEvent) {
		return getActionState();
	}

	@Override
	public void reset() {
		bBox = null;
		initialDragPoint = null;
		initialBBoxOrigin = null;
        StageManager stageManager = StageManager.getInstance();
        stageManager.getScene().setCursor(Cursor.DEFAULT);
	}
}
