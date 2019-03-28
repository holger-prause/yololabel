package playground.imagemarker.ui;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class PickLabelDialog {

	public static Stage dialogStage;
	public static boolean success;
	public boolean show() {
		success = false;
		Stage primaryStage = StageManager.getInstance().getPrimaryStage();	
        dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initStyle(StageStyle.UTILITY);
        dialogStage.initOwner(primaryStage);
        dialogStage.setTitle("Select Label");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/PickLabelDialog.fxml"));
        try {
			Parent root = loader.load();
			Scene dialogScene = new Scene(root, 300, 200);
            dialogStage.setScene(dialogScene);
            dialogStage.showAndWait();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
        
        return success;
	}
}
