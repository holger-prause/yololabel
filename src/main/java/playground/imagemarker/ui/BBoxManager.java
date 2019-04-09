package playground.imagemarker.ui;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
    private List<RepositoryEntry> repository = new ArrayList<>();
    private SimpleObjectProperty<BBox> selectedBBoxProperty = new SimpleObjectProperty<>(null);
    private RepositoryEntry currentEntry;
    private Image currentImage;
	private Path imageDirPath;

	private BBoxManager() {
    }

    public static BBoxManager getInstance() {
        if(instance == null) {
            instance = new BBoxManager();
        }
        return instance;
    }

    public void imageDirectorySelected(Path imageDirPath) {
		this.imageDirPath = imageDirPath;
		try {
    		//read in labels
    		Path labelsPath = imageDirPath.resolve("labels.txt");
    		if(Files.exists(labelsPath)) {
    			List<String> labels = Files.readAllLines(labelsPath);
				LabelsManager.getInstance().setLabels(labels);
    		} else {
                LabelsManager.getInstance().resetToPredefinedLabels();
            }

            repository.clear();
			List<Path> dirContent = Files.list(imageDirPath).collect(Collectors.toList());
			for(Path file: dirContent) {
				if(FileUtil.isImage(file)) {
					RepositoryEntry repositoryEntry 
						= new RepositoryEntry(file, new ArrayList<>());
					repository.add(repositoryEntry);
				}
			}

            clearCurrentEntry();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
    }

    public void goToImage(int index) {
        clearCurrentEntry();
        currentEntry = repository.get(index);
        initImage();
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

        clearCurrentEntry();
        currentEntry = repository.get(selectionIdx);
        initImage();
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

        clearCurrentEntry();
    	currentEntry = repository.get(selectionIdx);
        initImage();
    }

    private void initImage() {
        Path annFilePath = FileUtil.getAnnotationPath(currentEntry.path);
        currentImage = new Image(currentEntry.path.toUri().toString());
        if(Files.exists(annFilePath)) {
            List<BBox> bBoxes = convertFromYolo(annFilePath, currentImage.getWidth(), currentImage.getHeight());
            currentEntry.bBoxes = FXCollections.observableArrayList(bBoxes);
        }
    }

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
        serialize();
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

	private void serialize() {
		if(currentEntry != null) {
			Image img = new Image(currentEntry.path.toUri().toString());
			Path annFile = FileUtil.getAnnotationPath(currentEntry.path);
			
			try {
                if(currentEntry.bBoxes.isEmpty()) {
                    if(Files.exists(annFile)) {
                        Files.delete(annFile);
                    }
                } else {
                    Files.write(annFile, convertToYolo(currentEntry.bBoxes, img.getWidth(), img.getHeight()));
                }
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
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
        int lblIndex = Integer.parseInt(values[0]);


        if(lblIndex >= allLabels.size()) {
            int toAdd = lblIndex - allLabels.size() - 1;
            for(int i=0; i<toAdd; i++) {
                allLabels.add("out_of_index(undefined)");
            }
        }

        String label = allLabels.get(lblIndex);
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

	public boolean hasEntries() {
        return !repository.isEmpty();
    }

    public int getCurrentIndex() {
        if(currentEntry != null) {
            return repository.indexOf(currentEntry);
        }

        return -1;
    }

    public int getRepositorySize() {
        return repository.size();
    }

    public Image getCurrentImage() {
        return currentImage;
    }

	public Path getImageDirPath() {
		return imageDirPath;
	}

    public void moveCurrentImage(Path moveDir) throws IOException {
        if(currentEntry == null) {
            return;
        }

        Path targetImgPath = moveDir.resolve(currentEntry.path.getFileName());
        Files.move(currentEntry.path, targetImgPath, StandardCopyOption.REPLACE_EXISTING);

        Path targetAnnFile = null;
        Path currentAnnFile = FileUtil.getAnnotationPath(currentEntry.path);
        if(Files.exists(currentAnnFile)) {
            targetAnnFile = moveDir.resolve(currentAnnFile.getFileName());
            Files.move(currentAnnFile, targetAnnFile, StandardCopyOption.REPLACE_EXISTING);
        }

        //determine the next entry and select it if possible
        int selectionIdx = getCurrentIndex();
        repository.remove(currentEntry);
        clearCurrentEntry();

        if(!repository.isEmpty()) {
            int endIdx = repository.size() -1;
            if(selectionIdx > endIdx) {
                selectionIdx = endIdx - 1;
            }

            currentEntry = repository.get(selectionIdx);
            initImage();
        }
    }

    private void clearCurrentEntry() {
        currentEntry = null;
        selectedBBoxProperty.set(null);
        currentImage = null;
    }

    public class RepositoryEntry {
        private ObservableList<BBox> bBoxes;
        private Path path;

        public RepositoryEntry(Path path, List<BBox> bBoxes) {
            this.path = path;
            this.bBoxes = FXCollections.observableArrayList(bBoxes);
        }

        public ObservableList<BBox> getbBoxes() {
            return bBoxes;
        }

        public Path getPath() {
            return path;
        }
    }
}
