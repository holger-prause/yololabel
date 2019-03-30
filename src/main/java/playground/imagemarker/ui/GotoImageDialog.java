package playground.imagemarker.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by holger on 30.03.2019.
 */
public class GotoImageDialog {
    public static Stage dialogStage;
    public static Integer selectionResult;
    public Integer show() {
        Stage primaryStage = StageManager.getInstance().getPrimaryStage();
        dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initStyle(StageStyle.UTILITY);
        dialogStage.initOwner(primaryStage);
        dialogStage.setTitle("Goto Image");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/GotoImageDialog.fxml"));
        try {
            Parent root = loader.load();
            Scene dialogScene = new Scene(root);
            dialogStage.setScene(dialogScene);
            dialogStage.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return selectionResult;
    }
}
