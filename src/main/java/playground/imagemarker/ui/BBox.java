package playground.imagemarker.ui;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Point2D;

/**
 * Created by holger on 24.03.2019.
 */
public class BBox {
    private double w;
    private double h;
    private String label;
    private double x;
    private double y;
    private Point2D tl;
    private Point2D tr;
    private Point2D br;
    private Point2D bl;
    private BooleanProperty visibleProperty = new SimpleBooleanProperty(true);

    public BBox() {
        updatePoints();
    }

    public BBox(String label, double x, double y, double w, double h) {
        this.label = label;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        updatePoints();
    }

    public double getW() {
        return w;
    }

    public void setW(double w) {
        this.w = w;
        updatePoints();
    }

    public double getH() {
        return h;
    }

    public void setH(double h) {
        this.h = h;
        updatePoints();
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
        updatePoints();
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
        updatePoints();
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Point2D getTl() {
        return tl;
    }

    public Point2D getTr() {
        return tr;
    }

    public Point2D getBr() {
        return br;
    }

    public Point2D getBl() {
        return bl;
    }
    
    public double getSize() {
    	return w * h;
    }

    private void updatePoints() {
        tl = new Point2D(x, y);
        tr = new Point2D(x + w, y);
        br = new Point2D(x + w, y + h);
        bl = new Point2D(x, y + h);
    }

	@Override
	public String toString() {
		return label;
	}

	public BooleanProperty visibleProperty() {
		return visibleProperty;
	}
	
	
}
