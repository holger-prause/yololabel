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

    public enum DialogType {
        RENAME,
        COPY,
        SELECT
    }



	public boolean show(DialogType type) {
		success = false;
		Stage primaryStage = StageManager.getInstance().getPrimaryStage();	
        dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initStyle(StageStyle.UTILITY);
        dialogStage.initOwner(primaryStage);
        switch (type) {
            case SELECT:
                dialogStage.setTitle("Select Label");
                break;
            case RENAME:
                dialogStage.setTitle("Rename Label");
                break;
            case COPY:
                dialogStage.setTitle("Copy Label");
                break;
            default:
                throw new RuntimeException("Unsuuported type "+ type);
        }

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
