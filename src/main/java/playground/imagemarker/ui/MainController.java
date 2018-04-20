package playground.imagemarker.ui;


import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import playground.imagemarker.io.Config;
import playground.imagemarker.io.FileRepository;
import playground.imagemarker.io.SelectionRepository;

import java.io.*;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Created by Holger on 07.04.2018.
 */
public class MainController {

    private final double INITIAL_SCALE = 1.0;
    private final double SCALE_DELTA_FACTOR = 0.5;
    private final String BASE_TITLE = "Image Marker";
    private final int MAX_IMAGE_WIDTH = 1280;
    private final int MAX_IMAGE_HEIGHT = 720;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    protected Canvas imageDisplay;

    private Image selectedImage;
    private final DirectoryChooser directoryChooser;

    private DoubleProperty scale = new SimpleDoubleProperty(INITIAL_SCALE);
    private boolean selectionActive = false;

    private SelectionRepository selectionRepository;
    private FileRepository fileRepository;

    private Config config;

    public MainController() {
        directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(Paths.get(".").toFile());
        directoryChooser.setTitle("Select Directory with pictures");
    }

    @FXML
    public void initialize() {
        imageDisplay.setWidth(MAX_IMAGE_WIDTH);
        imageDisplay.setHeight(MAX_IMAGE_HEIGHT);
        imageDisplay.scaleXProperty().bind(scale);
        imageDisplay.scaleYProperty().bind(scale);
        scale.setValue(INITIAL_SCALE);
        imageDisplay.setDisable(true);
        selectedImage = null;
        selectionActive = false;
        selectionRepository = null;
        config = new Config();
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
            imageDisplay.setDisable(false);
            selectionRepository = new SelectionRepository(selectedDirectory);
            fileRepository = new FileRepository(selectedDirectory);
            if (fileRepository.hasFiles()) {
                selectFile(fileRepository.nextFile());
            }
        }
    }

    @FXML
    protected void onHelpClicked(ActionEvent event) {
        InputStream inputStream = getClass().getResourceAsStream("/helptext.txt");
        String helpText = new BufferedReader(new InputStreamReader(inputStream)).lines()
                .parallel().collect(Collectors.joining("\n"));

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Image Marker Help");
        alert.setHeaderText("");
        alert.setContentText(helpText);
        alert.showAndWait();
    }

    @FXML
    protected void onMouseScroll(ScrollEvent scrollEvent) {
        boolean zoomIn = scrollEvent.getDeltaY() > 0;
        if (zoomIn) {
            scale.setValue(scale.get() + SCALE_DELTA_FACTOR);
        } else {
            scale.setValue(scale.get() - SCALE_DELTA_FACTOR);
            if (scale.get() < INITIAL_SCALE) {
                scale.setValue(INITIAL_SCALE);
            }
        }

        if (imageDisplay.getBoundsInParent().getWidth() > scrollPane.getWidth()) {
            scrollPane.setHmax(imageDisplay.getWidth());
            scrollPane.setHvalue(scrollEvent.getX());
        } else {
            scrollPane.setHmax(0.0);
            scrollPane.setHvalue(0.0);
        }

        if (imageDisplay.getBoundsInParent().getHeight() > scrollPane.getHeight()) {
            scrollPane.setVmax(imageDisplay.getHeight());
            scrollPane.setVvalue(scrollEvent.getY());
        } else {
            scrollPane.setVmax(0.0);
            scrollPane.setVvalue(0.0);
        }
    }

    @FXML
    protected void onMouseClicked(final MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            selectionActive = !selectionActive;
            if (selectionActive) {
                final Selection selection;
                Optional<Selection> selectionOptional
                        = selectionRepository.getCurrentSelectionFor(event);
                if (selectionOptional.isPresent()) {
                    selection = selectionOptional.get();
                } else {
                    Optional<File> file = fileRepository.currentFile();
                    selection = new Selection(file.get().getName(), Selection.SelectionMode.ALL, new Point2D(event.getX(), event.getY()));
                }
                selectionRepository.addOrPushTop(selection);
            }
        }
    }

    @FXML
    protected void onMouseMoved(MouseEvent event) {
        if (selectionActive && selectionRepository.hasCurrentSelection()) {
            Selection selection = selectionRepository.getCurrentSelection();
            selection.adjust(event);
            repaint();
        }
    }

    @FXML
    public void onKeyReleased(KeyEvent event) {
        switch (event.getCode()) {
            case D:
                selectFile(fileRepository.nextFile());
                break;
            case W:
                if (selectionRepository.removeLastSelection()) {
                    repaint();
                }
                selectionActive = false;
                break;
            case A:
                selectFile(fileRepository.prevFile());
                break;
            case S:
                selectionRepository.save();
                break;
            default:
                break;
        }
    }

    private void selectFile(Optional<File> fileOptional) {
        if (fileOptional.isPresent()) {
            File selectedFile = fileOptional.get();
            selectedImage = new Image(selectedFile.toURI().toString());
            imageDisplay.setWidth(selectedImage.getWidth());
            imageDisplay.setHeight(selectedImage.getHeight());
            scale.setValue(INITIAL_SCALE);
            selectionRepository.setCurrentSelections(selectedFile.getName());

            Stage stage = (Stage) imageDisplay.getScene().getWindow();
            String title = "%s      %-35s Resolution %s x %s        File %d of %d";
            String format = String.format(title, BASE_TITLE, selectedFile.getName(), selectedImage.getWidth(), selectedImage.getHeight(), fileRepository.currentIndex() + 1, fileRepository.size());
            stage.setTitle(format);
            repaint();
        }
        selectionRepository.save();
    }

    private void repaint() {
        GraphicsContext graphicsContext2D = imageDisplay.getGraphicsContext2D();
        graphicsContext2D.drawImage(selectedImage, 0, 0);
        for (Selection selection : selectionRepository.getCurrentSelections()) {
            drawSelection(selection);
        }
    }

    private void drawSelection(Selection selection) {
        Rectangle rectangle = selection.getRectangle();
        GraphicsContext graphicsContext2D = imageDisplay.getGraphicsContext2D();
        graphicsContext2D.setLineWidth(3);
        graphicsContext2D.setStroke(Color.RED);
        graphicsContext2D.strokeRect(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
    }
}


