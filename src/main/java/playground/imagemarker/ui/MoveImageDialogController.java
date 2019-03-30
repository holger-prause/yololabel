package playground.imagemarker.ui;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.ResourceBundle;

/**
 * Created by holger on 30.03.2019.
 */
public class MoveImageDialogController implements Initializable{
    @FXML
    private TextField pathDisplay;

    @FXML
    private Button saveButton;

    private static Path moveImageDir;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(moveImageDir != null) {
            pathDisplay.setText(moveImageDir.toString());
            saveButton.setDisable(false);
            Platform.runLater(() -> saveButton.requestFocus());
        }
    }

    @FXML
    public void cancelClicked()
    {
        MoveImageDialog.dialogStage.close();
        MoveImageDialog.selectionResult = null;
    }

    @FXML
    public void saveClicked() {
        MoveImageDialog.selectionResult = moveImageDir;
        MoveImageDialog.dialogStage.close();
    }

    @FXML
    public void onKeyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER
                || event.getCode() == KeyCode.M) {
            if(!saveButton.isDisabled()) {
                saveClicked();
            }
            event.consume();
        }
    }

    public void browseClicked(MouseEvent event) {
        DirectoryChooser moveDirectoryChooser = new DirectoryChooser();
        moveDirectoryChooser.setTitle("Move Image Directory");
        if(moveImageDir == null) {
            moveDirectoryChooser.setInitialDirectory(BBoxManager.getInstance().getImageDirPath().toFile());
        } else {
            moveDirectoryChooser.setInitialDirectory(moveImageDir.toFile());
        }

        File selectedDir = moveDirectoryChooser.showDialog(StageManager.getInstance().getPrimaryStage());
        while (moveDirectoryChooser.getInitialDirectory().equals(selectedDir)) {
            selectedDir = moveDirectoryChooser.showDialog(StageManager.getInstance().getPrimaryStage());
        }

        if(selectedDir != null) {
            moveImageDir = selectedDir.toPath();
            pathDisplay.setText(moveImageDir.toString());
            saveButton.setDisable(false);
        }

        if(moveImageDir != null) {
            saveButton.requestFocus();
        }
    }
}
