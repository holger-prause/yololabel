package playground.imagemarker.ui.handler;

import javafx.geometry.Point2D;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import playground.imagemarker.ui.BBox;
import playground.imagemarker.ui.BBoxManager;
import playground.imagemarker.ui.LabelsManager;
import playground.imagemarker.ui.PickLabelDialog;
import playground.imagemarker.util.BBoxUtil;

/**
 * Created by holger on 23.03.2019.
 */
public class DrawLabelStateHandler extends UIStateHandler {
    private BBox boundingBox;
    private Point2D origin;

    public DrawLabelStateHandler() {
        super(ActionState.DRAW_LABEL);
    }

    @Override
    public void activate(ImageViewManager manager, InputEvent inputEvent) {
        MouseEvent mouseEvent = (MouseEvent)inputEvent;
        BBox bBox = BBoxManager.getInstance().getCurrentDrawingBox();
        if(bBox != null) {
            boundingBox = bBox;
            origin = boundingBox.getTl();
        } else {
            origin = new Point2D(mouseEvent.getX(), mouseEvent.getY());
            boundingBox = new BBox("", mouseEvent.getX(), mouseEvent.getY(), 0, 0);
            BBoxManager.getInstance().startDrawingBox(boundingBox);
        }
    }

    @Override
    public ActionState handleMouseMoved(ImageViewManager manager, MouseEvent mouseEvent) {
    	boolean withinBorder 
    		= manager.getImageDisplay().contains(mouseEvent.getX(), mouseEvent.getY());
        if(boundingBox != null && withinBorder) {
            BBoxUtil.resize(origin, boundingBox, mouseEvent);
            manager.repaint();
        }
        return getActionState();
    }

    @Override
    public ActionState handleMouseClicked(ImageViewManager manager, MouseEvent mouseEvent) {
        boolean success = false;
        if(boundingBox.getW() > 1 && boundingBox.getH() > 1) {
            PickLabelDialog pickLabelDialog = new PickLabelDialog();
            success = pickLabelDialog.show(PickLabelDialog.DialogType.SELECT);
            boundingBox.setLabel(LabelsManager.getInstance().getLastSelectedLabel());
        }

        BBoxManager.getInstance().endDrawingBox(success);
        return ActionState.VIEW_LABELS;
    }

    @Override
    public ActionState handleScrollEvent(ImageViewManager manager, ScrollEvent scrollEvent) {
        return getActionState();
    }

    @Override
    public void reset() {
        BBoxManager.getInstance().endDrawingBox(false);
        boundingBox = null;
        origin = null;
    }
}
