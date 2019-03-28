package playground.imagemarker.ui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class PickLabelDialogController implements Initializable{
	@FXML 
	ListView<String> labelsView;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		LabelsManager labelsManager = LabelsManager.getInstance();
		labelsView.getItems().addAll(labelsManager.getLabels());
		labelsView.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				// TODO Auto-generated method stub
				if(event.getClickCount() == 2) {
					saveClicked();
				} 
			}
		});
						
		String label = labelsManager.getLastSelectedLabel();
		if(label == null) {
			label = labelsManager.getLabels().get(0);
		}
		
		labelsView.getSelectionModel().select(label);	
		Platform.runLater(() -> {
			labelsView.requestFocus();
			labelsView.scrollTo(labelsView.getSelectionModel().getSelectedIndex());
		});
	}

	@FXML 
	public void cancelClicked() 
	{
		PickLabelDialog.dialogStage.close();
		PickLabelDialog.success = false;
	}

	@FXML 
	public void saveClicked() {
		String label = labelsView.getSelectionModel().getSelectedItem();
		LabelsManager labelsManager = LabelsManager.getInstance();
		labelsManager.setLastSelectedLabel(label);
		PickLabelDialog.success = true;
		PickLabelDialog.dialogStage.close();
	}

	@FXML 
	public void onKeyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
        	event.consume(); 
        	saveClicked();
        }
	}
}
