package playground.imagemarker.ui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Created by holger on 30.03.2019.
 */
public class GotoImageDialogController implements Initializable {

    @FXML
    private Label statusLabel;

    @FXML
    private TextField inputText;

    @FXML
    private Button saveButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        inputText.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                boolean valid = false;
                try {
                    int number = Integer.parseInt(newValue);
                    int repositorySize = BBoxManager.getInstance().getRepositorySize();

                    if(number < 1) {
                        statusLabel.setText("Cannot be smaller than 1");
                    } else if(number > repositorySize) {
                        statusLabel.setText("Cannot be greater than "+repositorySize);
                    } else {
                        statusLabel.setText("");
                        valid = true;
                    }
                } catch (NumberFormatException e) {
                    statusLabel.setText("No Valid Number");
                }

                saveButton.setDisable(!valid);
            }
        });
    }

    @FXML
    public void cancelClicked()
    {
        GotoImageDialog.dialogStage.close();
        GotoImageDialog.selectionResult = null;
    }

    @FXML
    public void saveClicked() {
        GotoImageDialog.selectionResult = Integer.parseInt(inputText.getText());
        GotoImageDialog.dialogStage.close();
    }

    @FXML
    public void onKeyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER
                || event.getCode() == KeyCode.G) {
            if(!saveButton.isDisabled()) {
                saveClicked();
            }
            event.consume();
        }

    }
}
