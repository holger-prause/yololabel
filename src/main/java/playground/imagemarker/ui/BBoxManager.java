package playground.imagemarker.ui;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;import java.util.function.Function;
import java.util.stream.Collectors;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import playground.imagemarker.util.FileUtil;

/**
 * Created by holger on 24.03.2019.
 */
public class BBoxManager {

    private static BBoxManager instance;
    private Map<Path, List<BBox>> boxes = new HashMap<>();
    
    private List<RepositoryEntry> repository = new ArrayList<>();
    private SimpleObjectProperty<BBox> selectedBBoxProperty = new SimpleObjectProperty<BBox>(null);
    private RepositoryEntry currentEntry;

    
    private BBoxManager() {
    }

    public static BBoxManager getInstance() {
        if(instance == null) {
            instance = new BBoxManager();
        }
        return instance;
    }

    public void imageDirectorySelected(Path imageDirPath) {
    	try {
    		//read in labels
    		Path labelsPath = imageDirPath.resolve("classes.txt");
    		if(Files.exists(labelsPath)) {
    			List<String> labels = Files.readAllLines(labelsPath);
				LabelsManager.getInstance().setLabels(labels);
    		} else {
    			
    		}
    		
			List<Path> dirContent = Files.list(imageDirPath).collect(Collectors.toList());
			for(Path file: dirContent) {
				if(FileUtil.isImage(file)) {
					Image img = new Image(file.toUri().toString());
					RepositoryEntry repositoryEntry 
						= new RepositoryEntry(file, new ArrayList<BBox>(), img.getWidth(), img.getHeight());
					String baseName = FileUtil.parseBaseName(file);
					String annFileName = baseName + ".txt";
					Path annFilePath = imageDirPath.toAbsolutePath().resolve(annFileName);
					if(Files.exists(annFilePath)) {
						List<BBox> bBoxes = convertFromYolo(annFilePath, img.getWidth(), img.getHeight());
						repositoryEntry.bBoxes = FXCollections.observableArrayList(bBoxes);
					}
					
					repository.add(repositoryEntry);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
    }
    
    public void nextImage() {
    	if(repository.isEmpty()) {
    		return;
    	}
    	
    	int selectionIdx;
    	if(currentEntry == null) {
    		selectionIdx = 0;
    	} else {
    		selectionIdx = repository.indexOf(currentEntry) + 1;
    	}

    	if(selectionIdx >= repository.size()) {
    		selectionIdx = repository.size() - 1;
    	}
  
    	currentEntry = repository.get(selectionIdx);
    }
    
    public void previousImage() {
    	if(repository.isEmpty()) {
    		return;
    	}
    	
    	int selectionIdx;
    	if(currentEntry == null) {
    		selectionIdx = 0;
    	} else {
    		selectionIdx = repository.indexOf(currentEntry) - 1;
    	}
    	
    	if(selectionIdx <= 0) {
    		selectionIdx = 0;
    	}
  
    	currentEntry = repository.get(selectionIdx);
    }
    
    
//    public void imageSelected(Path imgPath) {
//        currentImagePath = imgPath;
//        
//        
//        
//        currentViewBoxes.clear();
//        boxes.put(imgPath.toAbsolutePath(), currentViewBoxes);
//        selectedBBoxProperty.set(null);
//    }

    public void endDrawingBox(boolean success) {
        if(success) {
        	currentEntry.bBoxes.add(selectedBBoxProperty.get());
        } 
        selectedBBoxProperty.set(null);
        serialize();
    }
    
    public void removeCurrentDrawingBox() {
    	currentEntry.bBoxes.remove(selectedBBoxProperty.get());
    	selectedBBoxProperty.set(null);
    }

    public BBox getCurrentDrawingBox() {
        return selectedBBoxProperty.get();
    }

    public void startDrawingBox(BBox currentDrawingBox) {
    	selectedBBoxProperty.set(currentDrawingBox);
    }

	public SimpleObjectProperty<BBox> selectedBBoxProperty() {
		return selectedBBoxProperty;
	}
	
	public class RepositoryEntry {
		private ObservableList<BBox> bBoxes;
		private Path path;
		private double imWidth;
		private double imHeight;


		public RepositoryEntry(Path path, List<BBox> bBoxes, double imWidth, double imHeight) {
			this.path = path;
			this.bBoxes = FXCollections.observableArrayList(bBoxes);
			this.imWidth = imWidth;
			this.imHeight = imHeight;
		}


		public ObservableList<BBox> getbBoxes() {
			return bBoxes;
		}


		public Path getPath() {
			return path;
		}
	}
	
	private void serialize() {
		if(currentEntry != null) {
			Image img = new Image(currentEntry.path.toUri().toString());
			Path annFile = getAnnotationFile(currentEntry.path);
			
			try {
				Files.write(annFile, convertToYolo(currentEntry.bBoxes, img.getWidth(), img.getHeight()));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	private Path getAnnotationFile(Path imgPath) {
		String baseName = FileUtil.parseBaseName(currentEntry.path);
		String annFileName = baseName + ".txt";
		Path annFile = currentEntry.path.toAbsolutePath()
				.getParent().resolve(annFileName);
		return annFile;
	}
	
	private List<String> convertToYolo(List<BBox> boxes, double imWidth, double imHeight) {
		return boxes.stream().map(e -> convertToYolo(e, imWidth, imHeight))
			.collect(Collectors.toList());
	}
	
	private List<BBox> convertFromYolo(Path annFilePath, double imWidth, double imHeight) {
		try {
			List<String> yoloLines = Files.readAllLines(annFilePath);
			return yoloLines.stream().map(e -> convertFromYolo(e, imWidth, imHeight)).collect(Collectors.toList());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private BBox convertFromYolo(String line, double imWidth, double imHeight) {
		String[] values = line.split(" ");
		List<String> allLabels = LabelsManager.getInstance().getLabels();		
		String label = allLabels.get(Integer.parseInt(values[0]));
		double centerX = Double.parseDouble(values[1]);
		double centerY = Double.parseDouble(values[2]);
		
		double rW = Double.parseDouble(values[3]);
		double rH = Double.parseDouble(values[4]);
		double relX = centerX - (rW / 2.0); 
		double relY = centerY - (rH / 2.0);
		return new BBox(label, relX * imWidth, relY * imHeight, rW * imWidth, rH * imHeight);
	}
	
	private String convertToYolo(BBox bBox, double imWidth, double imHeight) {
	    //turn bbox into yolo format- bbox center relative to img width and height
	    double dw = 1. / imWidth;
	    double dh = 1. / imHeight;

	    double centerX = bBox.getX() + bBox.getW() / 2.0;
	    centerX = centerX * dw;

	    double centerY = bBox.getY() + bBox.getH() / 2.0;
	    centerY = centerY * dh;

	    double rw = bBox.getW() * dw;
	    double rh = bBox.getH() * dh;
	    
	    List<String> allLabels = LabelsManager.getInstance().getLabels();
	    int labelIdx = allLabels.indexOf(bBox.getLabel());
	    
	    NumberFormat nf = NumberFormat.getNumberInstance(Locale.ENGLISH);
	    nf.setMaximumFractionDigits(6);	    
	    return String.format("%s %s %s %s %s", labelIdx,  nf.format(centerX), 
	    		 nf.format(centerY),  nf.format(rw),  nf.format(rh));
	}

	public RepositoryEntry getCurrentEntry() {
		return currentEntry;
	}
}
