package playground.imagemarker.util;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.input.MouseEvent;
import playground.imagemarker.ui.BBox;
import playground.imagemarker.ui.handler.ImageViewManager;


/**
 * Created by holger on 24.03.2019.
 */
public class BBoxUtil {
    public enum CornerType {
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_RIGHT,
        BOTTOM_LEFT
    }

    public static BBox invertOrigin(BBox bBox, CornerType origin) {
        double newX;
        double newY;
        switch (origin) {
            case TOP_LEFT:
                newX = bBox.getX() + bBox.getW();
                newY = bBox.getY() + bBox.getH();
                break;
            case TOP_RIGHT:
                newX = bBox.getX();
                newY = bBox.getY() + bBox.getH();
                break;
            case BOTTOM_RIGHT:
                newX = bBox.getX();
                newY = bBox.getY();
                break;
            case BOTTOM_LEFT:
                newX = bBox.getX() + bBox.getW();
                newY = bBox.getY();
                break;
            default:
                throw new RuntimeException("Unhandled type: "+origin);
        }

        bBox.setX(newX);
        bBox.setY(newY);
        return bBox;
    }

    public static CornerType getResizeCorner(BBox bBox, MouseEvent mouseEvent) {
        final CornerType returnType;
        Point2D tl = new Point2D(bBox.getX(), bBox.getY());
        Point2D tr = new Point2D(bBox.getX()+ bBox.getW(), bBox.getY());
        Point2D br = new Point2D(tr.getX(), bBox.getY()+ bBox.getH());
        Point2D bl = new Point2D(bBox.getX(), br.getY());
        if(isCornerOverlap(tl, mouseEvent)) {
            returnType = CornerType.TOP_LEFT;
        }
        else if(isCornerOverlap(tr, mouseEvent)) {
            returnType = CornerType.TOP_RIGHT;
        }
        else if(isCornerOverlap(br, mouseEvent)) {
            returnType = CornerType.BOTTOM_RIGHT;
        }
        else if(isCornerOverlap(bl, mouseEvent)) {
            returnType = CornerType.BOTTOM_LEFT;
        }
        else {
            returnType = null;
        }

        return returnType;
    }
    
    public static boolean isWithinBBox(BBox bBox, MouseEvent mouseEvent) {
    	Rectangle2D rectangle = new Rectangle2D(bBox.getX(), bBox.getY(), bBox.getW(), bBox.getH());
    	return rectangle.contains(new Point2D(mouseEvent.getX(), mouseEvent.getY()));
    }

    private static boolean isCornerOverlap(Point2D corner, MouseEvent mouseEvent) {
        int delta = 4;
        double distX = Math.abs(mouseEvent.getX() - corner.getX());
        double distY = Math.abs(mouseEvent.getY() - corner.getY());
        return distX <= delta && distY <= delta;
    }
}
