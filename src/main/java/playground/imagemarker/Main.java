package playground.imagemarker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import playground.imagemarker.ui.MainController;

import java.io.IOException;

/**
 * Created by Holger on 07.04.2018.
 */
public class Main extends Application {
    @Override
    public void start(Stage stage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/UI.fxml"));
            Scene scene = new Scene(root);
            stage.setTitle("Image Marker");
            stage.setScene(scene);
            stage.show();
            root.requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}