package playground.imagemarker.ui.handler;

import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import playground.imagemarker.ui.BBoxManager;
import playground.imagemarker.ui.MoveImageDialog;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by holger on 30.03.2019.
 */
public class MoveImageHandler extends UIStateHandler {
    public MoveImageHandler() {
        super(ActionState.MOVE_IMAGE);
    }

    @Override
    public void activate(ImageViewManager manager, InputEvent inputEvent) {
        MoveImageDialog moveImageDialog = new MoveImageDialog();
        Path moveDir = moveImageDialog.show();
        if(moveDir != null) {
            try {
                BBoxManager.getInstance().moveCurrentImage(moveDir);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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
