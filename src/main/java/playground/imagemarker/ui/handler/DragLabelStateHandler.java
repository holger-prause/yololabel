package playground.imagemarker.ui.handler;

import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import playground.imagemarker.ui.*;
import playground.imagemarker.util.BBoxUtil;

public class DragLabelStateHandler extends UIStateHandler {
	private Point2D initialDragPoint;
	private Point2D initialBBoxOrigin;
	private BBox bBox; 
	
	public DragLabelStateHandler() {
		super(ActionState.DRAG_LABEL);
	}

	@Override
	public void activate(ImageViewManager manager, InputEvent inputEvent) {
        MouseEvent mouseEvent = (MouseEvent)inputEvent;
        bBox = BBoxManager.getInstance().getCurrentDrawingBox();
        initialDragPoint = new Point2D(mouseEvent.getX(), mouseEvent.getY());
        initialBBoxOrigin = new Point2D(bBox.getX(), bBox.getY());
	}

	@Override
	public ActionState handleMouseMoved(ImageViewManager manager, MouseEvent mouseEvent) {    	
    	BBox newLocation = calcNewLocation(mouseEvent);
    	if(BBoxUtil.isWithinImageView(newLocation, manager.getImageDisplay())) {
    		bBox.setX(newLocation.getX());
			bBox.setY(newLocation.getY());		
			manager.repaint();
    	}
    
		return getActionState();
	}
	
	private BBox calcNewLocation(MouseEvent mouseEvent) {
		BBox newPosition = new BBox("", bBox.getX(), bBox.getY(), bBox.getW(), bBox.getH());
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
		
		newPosition.setX(newX);
		newPosition.setY(newY);
		return newPosition;
	}
	
	@Override
	public ActionState handleMouseClicked(ImageViewManager manager, MouseEvent mouseEvent) {
        //rework whole stuff wtih normal mouse down and so on
        if(mouseEvent.getClickCount() == 2) {
            PickLabelDialog pickLabelDialog = new PickLabelDialog();
            boolean success = pickLabelDialog.show(PickLabelDialog.DialogType.RENAME);
            if(success) {
                bBox.setLabel(LabelsManager.getInstance().getLastSelectedLabel());
                BBoxManager.getInstance().endDrawingBox(success);
            }
        }
		return ActionState.VIEW_LABELS;
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
        BBoxManager.getInstance().endDrawingBox(false);
	}
}
