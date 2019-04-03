package playground.imagemarker.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import playground.imagemarker.io.Config;
import playground.imagemarker.ui.handler.ImageViewManager;
import playground.imagemarker.ui.handler.ViewLabelStateHandler;

/**
 * Created by Holger on 07.04.2018.
 */
public class MainController implements Initializable {

    private final int SCROLLBAR_WIDTH = 20;
    private final int SCROLLBAR_HEIGHT = 20;
    @FXML
    public BorderPane scrollPaneContent;

    @FXML
    public TextField imageNameLabel;

    @FXML
    private Parent root;

    @FXML
    private MenuBar menuBar;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    protected Canvas imageDisplay;
    
	@FXML 
	protected ListView<BBox> bboxListView;

    private final DirectoryChooser directoryChooser;
    private Config config;
    private ImageViewManager imageViewManager;

    public MainController() {
        directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(Paths.get(".").toFile());
        directoryChooser.setTitle("Select Directory with pictures");
    }

    private double calcScrollpaneWidth() {
        Scene scene = imageDisplay.getScene();
        return scene.getWidth();
    }

    private double calcScrollpaneHeight() {
        Scene scene = imageDisplay.getScene();
        return scene.getHeight() - menuBar.getHeight();
    }

    public void adjustUI(WindowEvent event) {
        imageDisplay.getScene().widthProperty().addListener((observable, oldValue, newValue)
                -> {
            double spW = calcScrollpaneWidth() - bboxListView.getWidth();
            scrollPane.setPrefWidth(spW);
            scrollPane.setMaxWidth(spW);
            scrollPaneContent.setMinWidth(spW - SCROLLBAR_HEIGHT);
        });

        imageDisplay.getScene().heightProperty().addListener((observable, oldValue, newValue)
                -> {
            double spH = calcScrollpaneHeight();
            scrollPane.setPrefHeight(spH);
            scrollPane.setMaxHeight(spH);
            scrollPaneContent.setMinHeight(spH - SCROLLBAR_WIDTH);
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        imageViewManager = new ImageViewManager(imageNameLabel, scrollPane, bboxListView, imageDisplay);
        config = new Config();
        try {
            List<String> labels = Files.readAllLines(config.getLabelFile());
            LabelsManager.getInstance().setLabels(new ArrayList<>(labels));
            LabelsManager.getInstance().setPredefinedLabels(new ArrayList<>(labels));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Optional<File> lastDir = config.getLastDir();
        if (lastDir.isPresent() && lastDir.get().exists()) {
            directoryChooser.setInitialDirectory(lastDir.get());
        }
    }

    @FXML
    protected void onDirectorySelected(ActionEvent event) {
        Stage stage = (Stage) imageDisplay.getScene().getWindow();
        File selectedDirectory = directoryChooser.showDialog(stage);
        if (selectedDirectory != null) {
            config.setLastDir(selectedDirectory);
            directoryChooser.setInitialDirectory(selectedDirectory);
            imageViewManager.handleImageDirSelected(selectedDirectory.toPath());
        }
    }

    @FXML
    protected void onHelpClicked(ActionEvent event) {
        InputStream inputStream = getClass().getResourceAsStream("/helptext.txt");
        String helpText = new BufferedReader(new InputStreamReader(inputStream)).lines()
                .parallel().collect(Collectors.joining("\n"));

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(ViewLabelStateHandler.BASE_TITLE+ " Help");
        alert.setHeaderText("");
        alert.setContentText(helpText);
        alert.showAndWait();
    }

    @FXML
    protected void onClassifierSelected() {
//        Stage stage = (Stage) imageDisplay.getScene().getWindow();
//        FileChooser fileChooser = new FileChooser();
//        fileChooser.setInitialDirectory(Paths.get(".").toFile());
//        fileChooser.getExtensionFilters().addAll(
//                new FileChooser.ExtensionFilter("XML", "*.xml")
//        );
//        File selectedClassifier = fileChooser.showOpenDialog(stage);
    }

    @FXML
    protected void onImageViewScroll(ScrollEvent event) {
        imageViewManager.handleScrollEvent(event);
        event.consume();
    }

    @FXML
    protected void onImageViewClicked(final MouseEvent event) {
        imageViewManager.handleMouseEvent(event);
        event.consume();
        
        
    }

    @FXML
    protected void onImageViewMoved(MouseEvent event) {
        imageViewManager.handleMouseEvent(event);
        event.consume();
    }

    @FXML
    public void onKeyReleased(KeyEvent event) {
        imageViewManager.handleKeyEvent(event);
    }

    public void onKeyPressed(KeyEvent event) {
        imageViewManager.handleKeyEvent(event);
    }

	@FXML 
	public void onRootClicked(MouseEvent event) {
		imageViewManager.handleOutsideClicked(event);
	}
}


