package playground.imagemarker.ui.handler;

import javafx.scene.Cursor;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import playground.imagemarker.ui.StageManager;

/**
 * Created by holger on 23.03.2019.
 */
public class AimLabelStateHandler extends LabelStateHandler {

    public AimLabelStateHandler() {
        super(ActionState.AIM_LABEL);
    }

    @Override
    public void activate(ImageViewManager manager) {
        StageManager stageManager = StageManager.getInstance();
        stageManager.getScene().setCursor(Cursor.CROSSHAIR);
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
