package playground.imagemarker.util;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import playground.imagemarker.ui.BBox;


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
    
    public static void resize(Point2D origin, BBox bBox, MouseEvent mouseEvent) {
    	double mX = mouseEvent.getX();
        double mY = mouseEvent.getY();
        double originX = origin.getX();
        double originY = origin.getY();
        
        boolean isLeft = mX < originX;
        boolean isTop = mY < originY;
        double newW = Math.abs(originX - mX);
        double newH = Math.abs(originY - mY);

        double newX = bBox.getX();
        double newY = bBox.getY();
        if(isLeft) {
            newX = originX - newW;
        }
        if(isTop) {
            newY = originY - newH;
        }

        //determine top x,y
        bBox.setX(newX);
        bBox.setY(newY);
        bBox.setW(newW);
        bBox.setH(newH);
    }
    
    public static boolean isWithinImageView(BBox bBox, Canvas imageDisplay) {
    	return imageDisplay.contains(bBox.getTl()) && imageDisplay.contains(bBox.getTr())
    			&& imageDisplay.contains(bBox.getBl()) && imageDisplay.contains(bBox.getBr());
    }
    
    public static boolean isWithinBBox(BBox bBox, MouseEvent mouseEvent) {
    	if(!bBox.visibleProperty().get()) {
    		return false;
    	}
    	
    	Rectangle2D rectangle = new Rectangle2D(bBox.getX(), bBox.getY(), bBox.getW(), bBox.getH());
    	return rectangle.contains(new Point2D(mouseEvent.getX(), mouseEvent.getY()));
    }

    private static boolean isCornerOverlap(Point2D corner, MouseEvent mouseEvent) {
        int delta = 6;
        double distX = Math.abs(mouseEvent.getX() - corner.getX());
        double distY = Math.abs(mouseEvent.getY() - corner.getY());
        return distX <= delta && distY <= delta;
    }
    
    public static Point2D getOppositeCorner(CornerType corner, BBox bBox) {
        switch (corner) {
	        case TOP_LEFT:
	            return bBox.getBr();
	        case TOP_RIGHT:
	            return bBox.getBl();
	        case BOTTOM_RIGHT:
	           return bBox.getTl();
	        case BOTTOM_LEFT:
	            return bBox.getTr();
	        default:
	            throw new RuntimeException("Unhandled type: "+corner);
        }
    }
}
