package playground.imagemarker.ui;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PickLabelDialog {
	public static String selectedLabel = null;
	
	public String show() {
		Stage primaryStage = StageManager.getInstance().getPrimaryStage();	
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(primaryStage);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/PickLabelDialog.fxml"));
        try {
			Parent root = loader.load();
			Scene dialogScene = new Scene(root, 300, 200);
            dialog.setScene(dialogScene);
            dialog.showAndWait();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
        
        return selectedLabel;
	}
}
