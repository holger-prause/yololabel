package playground.imagemarker.ui;

import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by holger on 23.03.2019.
 */
public class StageManager {
    private static StageManager instance;
    private static Stage primaryStage;

    private StageManager() {
    }

    public static StageManager getInstance() {
        if(instance == null) {
            instance = new StageManager();
        }
        return instance;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void setPrimaryStage(Stage primaryStage) {
        StageManager.primaryStage = primaryStage;
    }

    public Scene getScene() {
        return primaryStage.getScene();
    }
}