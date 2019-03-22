package playground.imagemarker.ui;


import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.*;
import playground.imagemarker.Main;
import playground.imagemarker.io.Config;
import playground.imagemarker.io.FileRepository;
import playground.imagemarker.io.SelectionRepository;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Created by Holger on 07.04.2018.
 */
public class MainController implements Initializable {

    private final double INITIAL_SCALE = 1.0;
    private final double SCALE_DELTA_FACTOR = 0.3;
    private final String BASE_TITLE = "Image Marker";
    private final int MAX_IMAGE_WIDTH = 1280;
    private final int MAX_IMAGE_HEIGHT = 720;
    private final int SCROLLBAR_WIDTH = 20;
    private final int SCROLLBAR_HEIGHT = 20;
    @FXML
    public BorderPane scrollPaneContent;

    @FXML
    private Parent root;

    @FXML
    private MenuBar menuBar;

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

    //private ClassifierService classifierService;

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
            double scrolPaneW = calcScrollpaneWidth() -20;
            scrollPane.setPrefWidth(scrolPaneW);
            scrollPane.setMaxWidth(scrolPaneW);
            //scrollPaneContent.setMinWidth(scrolPaneW - SCROLLBAR_HEIGHT);
            System.err.println("width change");
        });

        imageDisplay.getScene().heightProperty().addListener((observable, oldValue, newValue)
                -> {
            double spH = calcScrollpaneHeight() - 20;
            scrollPane.setPrefHeight(spH);
            scrollPane.setMaxHeight(spH);
            //scrollPaneContent.setMinHeight(spH - SCROLLBAR_WIDTH);

        });

        selectionRepository = new SelectionRepository(new File("C:\\development\\dataset\\open_images_lp_validation\\exclude"));
        fileRepository = new FileRepository(new File("C:\\development\\dataset\\open_images_lp_validation\\exclude"));
        if (fileRepository.hasFiles()) {
            imageDisplay.setDisable(false);
            selectFile(fileRepository.nextFile());
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                //Rectangle imViewDims = computeAvailableSpaceForImView();
                //scrollPane.setPrefWidth(imViewDims.getWidth());
                //scrollPane.setPrefHeight(imViewDims.getHeight());
            }
        });
    }

    private Rectangle computeAvailableSpaceForImView() {
        Scene scene = imageDisplay.getScene();
        double sceneHeight = scene.getHeight();



        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        return new Rectangle(0, 0, scene.getWidth(),
                sceneHeight - menuBar.getMinHeight());

/*        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        return new Rectangle(0, 0, screenBounds.getWidth(),
                screenBounds.getHeight() - menuBar.getHeight());*/
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
    protected void onClassifierSelected() {
        Stage stage = (Stage) imageDisplay.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(Paths.get(".").toFile());
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XML", "*.xml")
        );
        File selectedClassifier = fileChooser.showOpenDialog(stage);
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
        System.err.println("new w after scaling "+ imageDisplay.getWidth() + " ");
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
            } else {
                if (selectionRepository.hasCurrentSelection()
                        && !selectionRepository.getCurrentSelection().hasMinDimensions()) {
                    selectionRepository.removeLastSelection();
                    repaint();
                }
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
            case F:


                try {
                    String s = new File("C:\\development\\workspace\\imagemarker\\src\\main\\resources\\Main.css").toURI().toURL().toExternalForm();
                    root.getStylesheets().clear();
                    root.getStylesheets().add(s);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

            default:
                break;
        }
    }

    private void selectFile(Optional<File> fileOptional) {
        if (fileOptional.isPresent()) {
            File selectedFile = fileOptional.get();
            selectedImage = new Image(selectedFile.toURI().toString());
            scale.setValue(INITIAL_SCALE);
            imageDisplay.setWidth(selectedImage.getWidth());
            imageDisplay.setHeight(selectedImage.getHeight());
            selectionRepository.setCurrentSelections(selectedFile.getName());

            Stage stage = (Stage) imageDisplay.getScene().getWindow();
            String title = "%s      %-35s Resolution %s x %s        File %d of %d";
            String format = String.format(title, BASE_TITLE, selectedFile.getName(), selectedImage.getWidth(), selectedImage.getHeight(), fileRepository.currentIndex() + 1, fileRepository.size());
            stage.setTitle(format);

            //classifierService.classify(selectedFile.getAbsolutePath());
            repaint();
        }
        selectionRepository.save();
    }


    private void repaint() {
        GraphicsContext graphicsContext2D = imageDisplay.getGraphicsContext2D();
        graphicsContext2D.clearRect(0, 0, imageDisplay.getWidth(), imageDisplay.getHeight());
        graphicsContext2D.drawImage(selectedImage, 0, 0);
        for (Selection selection : selectionRepository.getCurrentSelections()) {
            drawSelection(selection);
        }

/*        for (Match match : classifierService.getMatches()) {
            Rectangle rectangle = match.getRectangle();
            graphicsContext2D.setLineWidth(3);
            graphicsContext2D.setStroke(Color.GREEN);
            graphicsContext2D.strokeRect(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
        }*/
    }

    private void drawSelection(Selection selection) {
        Rectangle rectangle = selection.getRectangle();
        GraphicsContext graphicsContext2D = imageDisplay.getGraphicsContext2D();
        graphicsContext2D.setLineWidth(3);
        graphicsContext2D.setStroke(Color.RED);
        graphicsContext2D.strokeRect(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
    }
}


