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

    public static Stage stage;

    @Override
    public void start(Stage stage) {





        try {
            Parent root = FXMLLoader.load(getClass().getResource("/UI.fxml"));
            Scene scene = new Scene(root);

            stage.setTitle("Image Marker");
            stage.setScene(scene);
            Main.stage = stage;
            stage.show();




            root.requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String title = "%s      %-35s Resolution %s x %s        File %d of %35d";
        String format = String.format(title, "Image Marker", "image_8.png", 1280, 760, 5, 10);
        System.err.println(format);

        format = String.format(title, "Image Marker", "8.png", 1280, 760, 10, 10);
        format = String.format(title, "Image Marker", "fucking_long_name_of_file.png", 1280, 760, 10, 10);
        System.err.println(format);


        launch(args);
    }
}