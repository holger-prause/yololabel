package playground.imagemarker.ui;


import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.*;
import javafx.util.Callback;
import playground.imagemarker.io.Config;
import playground.imagemarker.io.FileRepository;
import playground.imagemarker.io.SelectionRepository;
import playground.imagemarker.ui.handler.ImageViewManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.scene.control.ListView;

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
    
	@FXML 
	protected ListView<BBox> bboxListView;

    private Image selectedImage;
    private final DirectoryChooser directoryChooser;

    private DoubleProperty scale = new SimpleDoubleProperty(INITIAL_SCALE);
    private boolean selectionActive = false;

    private SelectionRepository selectionRepository;
    private FileRepository fileRepository;

    private Config config;
    private ImageViewManager imageViewManager;


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
        imageViewManager = new ImageViewManager(bboxListView, imageDisplay);
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

//        Platform.runLater(new Runnable() {
//            @Override
//            public void run() {
//                //Rectangle imViewDims = computeAvailableSpaceForImView();
//                //scrollPane.setPrefWidth(imViewDims.getWidth());
//                //scrollPane.setPrefHeight(imViewDims.getHeight());
//            }
//        });
        
                
        
    }

    @FXML
    protected void onDirectorySelected(ActionEvent event) {
        Stage stage = (Stage) imageDisplay.getScene().getWindow();
        File selectedDirectory = directoryChooser.showDialog(stage);
        if (selectedDirectory != null) {
            config.setLastDir(selectedDirectory);
            directoryChooser.setInitialDirectory(selectedDirectory);
            imageDisplay.setDisable(false);
            BBoxManager.getInstance()
            	.imageDirectorySelected(selectedDirectory.toPath());
            
//            selectionRepository = new SelectionRepository(selectedDirectory);
//            fileRepository = new FileRepository(selectedDirectory);
//            if (fileRepository.hasFiles()) {
//                selectFile(fileRepository.nextFile());
//            }
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
    protected void onImageViewScroll(ScrollEvent event) {
        imageViewManager.handleScrollEvent(event);
        event.consume();
/*        boolean zoomIn = scrollEvent.getDeltaY() > 0;
        if (zoomIn) {
            scale.setValue(scale.get() + SCALE_DELTA_FACTOR);
        } else {
            scale.setValue(scale.get() - SCALE_DELTA_FACTOR);
            if (scale.get() < INITIAL_SCALE) {
                scale.setValue(INITIAL_SCALE);
            }
        }*/
    }

    @FXML
    protected void onImageViewClicked(final MouseEvent event) {
    	System.err.println("on imgview clicked clicked");
    	
        imageViewManager.handleMouseEvent(event);
        event.consume();
        /*if (event.getButton() == MouseButton.PRIMARY) {
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
        }*/
    }

    @FXML
    protected void onImageViewMoved(MouseEvent event) {
        imageViewManager.handleMouseEvent(event);
        event.consume();
/*        if (selectionActive && selectionRepository.hasCurrentSelection()) {
            Selection selection = selectionRepository.getCurrentSelection();
            selection.adjust(event);
            repaint();
        }*/
    }

    @FXML
    public void onKeyReleased(KeyEvent event) {
        imageViewManager.handleKeyEvent(event);

        switch (event.getCode()) {
//            case D:
//                selectFile(fileRepository.nextFile());
//                break;
//            case W:
//                if (selectionRepository.removeLastSelection()) {
//                    repaint();
//                }
//                selectionActive = false;
//                break;
//            case A:
//                selectFile(fileRepository.prevFile());
//                break;
//            case S:
//                selectionRepository.save();
//                break;
            case F:


//                try {
//                    String s = new File("C:\\development\\workspace\\imagemarker\\src\\main\\resources\\Main.css").toURI().toURL().toExternalForm();
//                    root.getStylesheets().clear();
//                    root.getStylesheets().add(s);
//
//                } catch (MalformedURLException e) {
//                    e.printStackTrace();
//                }

            default:
                break;
        }
    }
    
	@FXML 
	public void onRootClicked(MouseEvent event) {
		imageViewManager.handleOutsideClicked();
	}

    private void selectFile(Optional<File> fileOptional) {
//        if (fileOptional.isPresent()) {
//            File selectedFile = fileOptional.get();
//            imageViewManager.imageSelected(selectedFile.toPath().toAbsolutePath());
//
//            selectedImage = new Image(selectedFile.toURI().toString());
//            scale.setValue(INITIAL_SCALE);
//            imageDisplay.setWidth(selectedImage.getWidth());
//            imageDisplay.setHeight(selectedImage.getHeight());
//            selectionRepository.setCurrentSelections(selectedFile.getName());
//
//            Stage stage = (Stage) imageDisplay.getScene().getWindow();
//            String title = "%s      %-35s Resolution %s x %s        File %d of %d";
//            String format = String.format(title, BASE_TITLE, selectedFile.getName(), selectedImage.getWidth(), selectedImage.getHeight(), fileRepository.currentIndex() + 1, fileRepository.size());
//            stage.setTitle(format);
//
//            //classifierService.classify(selectedFile.getAbsolutePath());
//            repaint();
//        }
//        selectionRepository.save();
    }


    private void repaint() {
/*        GraphicsContext graphicsContext2D = imageDisplay.getGraphicsContext2D();
        graphicsContext2D.clearRect(0, 0, imageDisplay.getWidth(), imageDisplay.getHeight());
        graphicsContext2D.drawImage(selectedImage, 0, 0);
        for (Selection selection : selectionRepository.getCurrentSelections()) {
            drawSelection(selection);
        }*/

/*        for (Match match : classifierService.getMatches()) {
            Rectangle rectangle = match.getRectangle();
            graphicsContext2D.setLineWidth(3);
            graphicsContext2D.setStroke(Color.BBOX_BORDER_COLOR);
            graphicsContext2D.strokeRect(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
        }*/
    }
}


