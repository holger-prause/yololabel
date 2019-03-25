package playground.imagemarker.ui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class PickLabelDialogController implements Initializable{

	private List<String> labels = new ArrayList<String>();
	
	@FXML 
	ListView<String> labelsView;
	
	



	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		labels.add("car");
		labels.add("window");
		labels.add("dog");
		labelsView.getItems().addAll(labels);
		
		labelsView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				
				
			}
		});
	}

	@FXML public void cancelClicked() 
	{
		PickLabelDialog.selectedLabel = null;
		
	}

	@FXML 
	public void saveClicked() {
		PickLabelDialog.selectedLabel = labelsView.getSelectionModel().getSelectedItem();
	}
}
