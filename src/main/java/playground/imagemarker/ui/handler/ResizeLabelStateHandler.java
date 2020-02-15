package playground.imagemarker.ui.handler;

import javafx.geometry.Point2D;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import playground.imagemarker.ui.BBox;
import playground.imagemarker.ui.BBoxManager;
import playground.imagemarker.util.BBoxUtil;

public class ResizeLabelStateHandler extends UIStateHandler {
	private BBox resizeBBox;
	private Point2D origin;
	
	public ResizeLabelStateHandler() {
		super(ActionState.RESIZE_LABEL);
	}
	
	@Override
	public void activate(ImageViewManager manager, InputEvent inputEvent) {
        MouseEvent mouseEvent = (MouseEvent)inputEvent;
        resizeBBox  = BBoxManager.getInstance().getCurrentDrawingBox();
        BBoxUtil.CornerType resizeCorner = BBoxUtil.getResizeCorner(resizeBBox , mouseEvent);
        origin = BBoxUtil.getOppositeCorner(resizeCorner, resizeBBox);
	}

	@Override
	public ActionState handleMouseMoved(ImageViewManager manager, MouseEvent mouseEvent) {
        if(resizeBBox != null) {
            BBoxUtil.resize(origin, resizeBBox, mouseEvent);
            manager.repaint();
        }
        return getActionState();
	}

	@Override
	public ActionState handleMouseClicked(ImageViewManager manager, MouseEvent mouseEvent) {
		return ActionState.VIEW_LABELS;
	}

	@Override
	public ActionState handleScrollEvent(ImageViewManager manager, ScrollEvent scrollEvent) {
		return getActionState();
	}

	@Override
	public void reset() {
		resizeBBox = null;
		origin = null;
		BBoxManager.getInstance().endDrawingBox(false);
	}
}
