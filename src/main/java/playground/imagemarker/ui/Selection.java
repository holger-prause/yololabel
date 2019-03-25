package playground.imagemarker.ui;

import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;


/**
 * Created by Holger on 19.04.2018.
 */
public class Selection {

    private String fileName;
    private SelectionMode mode;
    private final Rectangle rectangle;
    private final double BORDER_DELTA = 5;

    private final double MIN_HEIGHT = 2 * BORDER_DELTA + 2;
    private final double MIN_WIDTH = MIN_HEIGHT;

    public enum SelectionMode {
        ALL,
        HEIGHT_BOTTOM,
        HEIGHT_TOP,
        WIDTH_LEFT,
        WIDTH_RIGHT
    }

    public Selection(String fileName, double x, double y, double width, double height) {
        this.fileName = fileName;
        this.mode = SelectionMode.ALL;
        rectangle = new Rectangle(x, y, width, height);
    }

    public Selection(String fileName, SelectionMode mode, Point2D sourcePoint) {
        this.fileName = fileName;
        this.mode = mode;
        rectangle = new Rectangle(sourcePoint.getX(), sourcePoint.getY(), 0, 0);
    }

    public boolean hasMinDimensions() {
        return rectangle.getWidth() > MIN_WIDTH && rectangle.getHeight() > MIN_HEIGHT;
    }

    public void adjust(MouseEvent event) {
        final double sourceX = rectangle.getX();
        final double sourceY = rectangle.getY();
        final double eventX = event.getX();
        final double eventY = event.getY();

        final double width = sourceX < eventX ? eventX - sourceX : sourceX - eventX;
        final double height = sourceY < eventY ? eventY - sourceY : sourceY - eventY;

        switch (mode) {
            case HEIGHT_TOP:
                double deltaY = eventY - rectangle.getY();
                deltaY = adjustHeight(deltaY);
                rectangle.setY(rectangle.getY() + deltaY);
                break;
            case HEIGHT_BOTTOM:
                deltaY = (rectangle.getY() + rectangle.getHeight()) - eventY;
                adjustHeight(deltaY);
                break;
            case WIDTH_LEFT:
                double deltaX = eventX - rectangle.getX();
                deltaX = adjustWidth(deltaX);
                rectangle.setX(rectangle.getX() + deltaX);
                break;
            case WIDTH_RIGHT:
                deltaX = (rectangle.getX() + rectangle.getWidth()) - eventX;
                adjustWidth(deltaX);
                break;
            case ALL:
                rectangle.setWidth(width);
                rectangle.setHeight(height);
                break;
            default:
                throw new RuntimeException("Unexpetected selection mode");
        }
    }

    private double adjustWidth(double deltaX) {
        double width = rectangle.getWidth();
        if (width - deltaX <= MIN_WIDTH) {
            rectangle.setWidth(MIN_WIDTH);
            return width - MIN_WIDTH;
        }
        rectangle.setWidth(width - deltaX);
        return deltaX;
    }

    private double adjustHeight(double deltaY) {
        double height = rectangle.getHeight();
        if (height - deltaY <= MIN_HEIGHT) {
            rectangle.setHeight(MIN_HEIGHT);
            return height - MIN_HEIGHT;
        }
        rectangle.setHeight(height - deltaY);
        return deltaY;
    }

    public String getFileName() {
        return fileName;
    }

    public boolean doSelect(MouseEvent event) {
        if (isTopClicked(event)) {
            mode = SelectionMode.HEIGHT_TOP;
            return true;
        } else if (isBottomClicked(event)) {
            mode = SelectionMode.HEIGHT_BOTTOM;
            return true;
        } else if (isLeftClicked(event)) {
            mode = SelectionMode.WIDTH_LEFT;
            return true;
        } else if (isRightClicked(event)) {
            mode = SelectionMode.WIDTH_RIGHT;
            return true;
        }
        return false;
    }

    public boolean isTopClicked(MouseEvent event) {
        return isOnHorizontalLine(event, 0);
    }

    public boolean isBottomClicked(MouseEvent event) {
        return isOnHorizontalLine(event, rectangle.getHeight());
    }

    public boolean isLeftClicked(MouseEvent event) {
        return isOnVerticalLine(event, 0);
    }

    public boolean isRightClicked(MouseEvent event) {
        return isOnVerticalLine(event, rectangle.getWidth());
    }

    private boolean isOnHorizontalLine(MouseEvent event, double height) {
        return rectangle.getX() - BORDER_DELTA < event.getX() && event.getX() < rectangle.getX() + rectangle.getWidth() + BORDER_DELTA
                && (rectangle.getY() + height - BORDER_DELTA < event.getY()
                && event.getY() < rectangle.getY() + height + BORDER_DELTA);
    }

    private boolean isOnVerticalLine(MouseEvent event, double width) {
        return rectangle.getY() - BORDER_DELTA < event.getY() && event.getY() < rectangle.getY() + BORDER_DELTA + rectangle.getHeight()
                && (rectangle.getX() + width - BORDER_DELTA < event.getX() && event.getX() < rectangle.getX() + width + BORDER_DELTA);
    }

    public Rectangle getRectangle() {
        return rectangle;
    }
}
