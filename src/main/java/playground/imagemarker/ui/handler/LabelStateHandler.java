package playground.imagemarker.ui.handler;

import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

/**
 * Created by holger on 23.03.2019.
 */
public abstract class LabelStateHandler {
    private final ActionState actionState;
    public LabelStateHandler(ActionState actionState) {
        this.actionState = actionState;
    }

    public abstract void activate(ImageViewManager manager);
    public abstract ActionState handleMouseMoved(ImageViewManager manager, MouseEvent mouseEvent);
    public abstract ActionState handleMouseClicked(ImageViewManager manager, MouseEvent mouseEvent);
    public abstract ActionState handleScrollEvent(ImageViewManager manager, ScrollEvent scrollEvent);
    public abstract void reset();

    public ActionState getActionState() {
        return actionState;
    }


}
