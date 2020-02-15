package playground.imagemarker.ui.handler;

import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import playground.imagemarker.ui.BBoxManager;
import playground.imagemarker.ui.GotoImageDialog;

/**
 * Created by holger on 30.03.2019.
 */
public class GotoImageHandler extends UIStateHandler {

    public GotoImageHandler() {
        super(ActionState.GOTO_IMAGE);
    }

    @Override
    public void activate(ImageViewManager manager, InputEvent inputEvent) {
        GotoImageDialog gotoImageDialog = new GotoImageDialog();
        Integer imgNr = gotoImageDialog.show();
        if(imgNr != null) {
            BBoxManager.getInstance().goToImage(imgNr-1);
        }
    }

    @Override
    public ActionState handleMouseMoved(ImageViewManager manager, MouseEvent mouseEvent) {
        return getActionState();
    }

    @Override
    public ActionState handleMouseClicked(ImageViewManager manager, MouseEvent mouseEvent) {
        return getActionState();
    }

    @Override
    public ActionState handleScrollEvent(ImageViewManager manager, ScrollEvent scrollEvent) {
        return getActionState();
    }

    @Override
    public void reset() {

    }
}
