package playground.imagemarker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import playground.imagemarker.ui.MainController;
import playground.imagemarker.ui.StageManager;
import wrapper.LibLoader;

import java.io.IOException;

/**
 * Created by Holger on 07.04.2018.
 */
public class Main extends Application {
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Main.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setTitle("Image Marker");
            stage.setScene(scene);
            stage.setMaximized(true);


            //set some inital width and height for window

            Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
            stage.setX(bounds.getMinX());
            stage.setY(bounds.getMinY());
            stage.setWidth(bounds.getWidth());
            stage.setHeight(bounds.getHeight());

            //do ui adjustments after scene is available
            //in the initialize method the scene is null
            MainController controller = loader.getController();
            stage.setOnShown(controller::adjustUI);

            StageManager stageManager = StageManager.getInstance();
            stageManager.setPrimaryStage(stage);

            stage.show();
            root.requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        //OpenCvLoader openCvLoader = new OpenCvLoader();
        //openCvLoader.loadLibs();
        LibLoader.loadLibs();
        launch(args);
    }
}