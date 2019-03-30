package playground.imagemarker.ui.handler;

import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import playground.imagemarker.ui.BBoxManager;
import playground.imagemarker.ui.StageManager;

/**
 * Created by holger on 23.03.2019.
 */
public class AimLabelStateHandler extends UIStateHandler {

    public AimLabelStateHandler() {
        super(ActionState.AIM_LABEL);
    }

    @Override
    public void activate(ImageViewManager manager) {  
    	//clear any previous selection
    	BBoxManager.getInstance().endDrawingBox(false);
    	
        StageManager stageManager = StageManager.getInstance();
        stageManager.getScene().setCursor(Cursor.CROSSHAIR);
        manager.repaint();
    }

    @Override
    public ActionState handleMouseMoved(ImageViewManager manager, MouseEvent mouseEvent) {
        return getActionState();
    }

    @Override
    public ActionState handleMouseClicked(ImageViewManager manager, MouseEvent mouseEvent) {
        return ActionState.DRAW_LABEL;
    }

    @Override
    public ActionState handleScrollEvent(ImageViewManager manager, ScrollEvent scrollEvent) {
        return getActionState();
    }

    @Override
    public void reset() {
        StageManager stageManager = StageManager.getInstance();
        stageManager.getScene().setCursor(Cursor.DEFAULT);
    }
}
