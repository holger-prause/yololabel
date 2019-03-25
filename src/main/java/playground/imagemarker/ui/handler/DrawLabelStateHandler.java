package playground.imagemarker.ui.handler;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import playground.imagemarker.ui.BBox;
import playground.imagemarker.ui.BBoxManager;

/**
 * Created by holger on 23.03.2019.
 */
public class DrawLabelStateHandler extends LabelStateHandler {
    private BBox boundingBox;
    private double originX = 0;
    private double originY = 0;

    public DrawLabelStateHandler() {
        super(ActionState.DRAW_LABEL);
    }

    @Override
    public void activate(ImageViewManager manager) {
        BBox bBox = BBoxManager.getInstance().getCurrentDrawingBox();
        if(bBox != null) {
            boundingBox = bBox;
            originY = boundingBox.getY();
            originX = boundingBox.getX();
        }
    }

    @Override
    public ActionState handleMouseMoved(ImageViewManager manager, MouseEvent mouseEvent) {
        if(boundingBox != null) {
            //adjust height and width
            double mX = mouseEvent.getX();
            double mY = mouseEvent.getY();

            boolean isLeft = mX < originX;
            boolean isTop = mY < originY;
            double newW = Math.abs(originX - mX);
            double newH = Math.abs(originY - mY);

            double newX = boundingBox.getX();
            double newY = boundingBox.getY();
            if(isLeft) {
                newX = originX - newW;
            }
            if(isTop) {
                newY = originY - newH;
            }

            //determine top x,y
            boundingBox.setX(newX);
            boundingBox.setY(newY);
            boundingBox.setW(newW);
            boundingBox.setH(newH);
            manager.repaint();
        }

        return getActionState();
    }

    @Override
    public ActionState handleMouseClicked(ImageViewManager manager, MouseEvent mouseEvent) {
        ActionState returnState = getActionState();
        if(boundingBox == null) {
            originX = mouseEvent.getX();
            originY = mouseEvent.getY();
            boundingBox = new BBox("", mouseEvent.getX(), mouseEvent.getY(), 0, 0);
            BBoxManager.getInstance().startDrawingBox(boundingBox);
        } else {
            boolean success = false;
            if(boundingBox.getW() > 1 && boundingBox.getH() > 1) {
                success = true;
            }
            BBoxManager.getInstance().endDrawingBox(success);
            mouseEvent.consume();
            returnState = ActionState.VIEW_LABELS;
        }

        return returnState;
    }

    @Override
    public ActionState handleScrollEvent(ImageViewManager manager, ScrollEvent scrollEvent) {
        return getActionState();
    }

    @Override
    public void reset() {
        BBoxManager.getInstance().endDrawingBox(false);
        boundingBox = null;
        originX = 0;
        originY = 0;
    }
}
