package playground.imagemarker.ui;

import java.io.IOException;
import java.nio.file.*;
import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import playground.imagemarker.io.FileWatcher;
import playground.imagemarker.io.IFileWatchNotifieable;
import playground.imagemarker.ui.handler.ImageViewManager;
import playground.imagemarker.util.FileUtil;

/**
 * Created by holger on 24.03.2019.
 */
public class BBoxManager implements IFileWatchNotifieable{

    private static BBoxManager instance;
    private List<RepositoryEntry> repository = new ArrayList<>();
    private SimpleObjectProperty<BBox> selectedBBoxProperty = new SimpleObjectProperty<>(null);
    private RepositoryEntry currentEntry;
    private Image currentImage;
	private Path imageDirPath;
    private boolean scanImageDir;
    private FileWatcher fileWatcher;

	private BBoxManager() {
    }

    public static BBoxManager getInstance() {
        if(instance == null) {
            instance = new BBoxManager();
        }
        return instance;
    }

    public void listToIndex() {
        int index = repository.indexOf(currentEntry);
        for(int i = 0; i<= index; i++) {
            if(i < 0 || i >= repository.size()) {
                return;
            }
            RepositoryEntry entry = repository.get(i);
            System.err.println(entry.getPath().toAbsolutePath().toString());
        }
    }

    public void imageDirectorySelected(Path imageDirPath, ImageViewManager viewManager) {
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
            long start = System.currentTimeMillis();

			List<Path> dirContent = Files.list(imageDirPath).collect(Collectors.toList());
			for(Path file: dirContent) {
				if(FileUtil.isImage(file)) {
					RepositoryEntry repositoryEntry 
						= new RepositoryEntry(file);
					repository.add(repositoryEntry);
				}
			}
            System.err.println("read in in " + (System.currentTimeMillis() - start) + " ms");
            clearCurrentEntry();

            if(fileWatcher == null) {
                fileWatcher = new FileWatcher(imageDirPath, true, this, viewManager);
                fileWatcher.setNotify(scanImageDir);
                fileWatcher.start();
            }
            fileWatcher.changeDir(imageDirPath);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
    }

    public void goToImage(int index) {
        clearCurrentEntry();
        currentEntry = repository.get(index);
        initImage();
    }

    @Override
    public void fileCreated(Path file) {
        Platform.runLater(() -> {
            //replace current element and switch to it
            int currIdx = repository.indexOf(currentEntry);
            RepositoryEntry repositoryEntry = new RepositoryEntry(file);
            if(currIdx == -1) {
                repository.add(repositoryEntry);
                goToImage(0);
            } else {
                repository.add(currIdx, repositoryEntry);
                goToImage(currIdx);
            }
        });
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
            currentEntry.updateEntries(bBoxes);

            /*Collections.sort(bBoxes, (o1, o2) -> {
                String o1First = o1.getLabel().substring(0,1).toLowerCase();
                String o2First = o2.getLabel().substring(0,1).toLowerCase();
                return o1First.compareTo(o2First);
            });
            currentEntry.model = FXCollections.observableArrayList(bBoxes);*/

            //currentEntry.model.clear();
            //currentEntry.model.addAll(bBoxes);
            //Collections.sort(currentEntry.model, (o1, o2) -> o1.getLabel().compareTo(o2.getLabel()));
/*            currentEntry.model.add(new BBox("z", 0, 0, 0, 0));
            currentEntry.model.add(new BBox("a", 0, 0, 0, 0));
            currentEntry.model.add(new BBox("s", 0, 0, 0, 0));*/

/*            ObservableList<BBox> observableList = FXCollections.observableArrayList(bBoxes);


            SortedList<BBox> sortedList = new SortedList<>( observableList,
                    (BBox box1, BBox box2) -> box1.getLabel().compareTo(box2.getLabel()));


            Collections.sort(bBoxes, (o1, o2) -> o1.getLabel().compareTo(o2.getLabel()));
            currentEntry.bBoxes = FXCollections.observableArrayList(bBoxes);*/
        }
    }

    public void addBBox(BBox bBox) {
        if(currentEntry != null) {
            currentEntry.bBoxes.add(bBox);
            serialize();
        }
    }

    public void endDrawingBox(boolean success) {
        BBox selectedBBox = selectedBBoxProperty.get();
        if(success) {
            if(!currentEntry.bBoxes.contains(selectedBBox)) {
                currentEntry.bBoxes.add(selectedBBox);
            }
            else {
                //refresh view
                ArrayList<BBox> copy = new ArrayList<>(currentEntry.bBoxes);
                currentEntry.bBoxes.clear();
                currentEntry.bBoxes.addAll(copy);
            }
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
        //stupid javafx does not let add you to sorted list
        private ObservableList<BBox> bBoxes;
        private Path path;

        public RepositoryEntry(Path path) {
            this.path = path;
            //sort by labels
            bBoxes = FXCollections.observableArrayList(new ArrayList<>());
            bBoxes.addListener((ListChangeListener<BBox>) c -> {
                if(c.next()) {
                    if(c.wasAdded() || c.wasRemoved()) {
                        sortEntries();
                    }
                }
            });
        }

        public void updateEntries(List<BBox> bBoxes) {
            this.bBoxes.clear();
            for(BBox bBox: bBoxes) {
                ChangeListener<String> labelListener = (observable, oldValue, newValue) -> {
                    sortEntries();
                };
                bBox.getLabelProperty().addListener(labelListener);
            }
            this.bBoxes.addAll(bBoxes);
        }

        private void sortEntries() {
            Collections.sort(bBoxes, (o1, o2) -> {
                String o1First = o1.getLabel().substring(0,1).toLowerCase();
                String o2First = o2.getLabel().substring(0,1).toLowerCase();
                return o1First.compareTo(o2First);
            });
        }

        public ObservableList<BBox> getbBoxes() {
            return bBoxes;
        }

        public Path getPath() {
            return path;
        }
    }

    public void setScanImageDir(boolean scanImageDir) {
        if (fileWatcher != null) {
            fileWatcher.setNotify(scanImageDir);
        }
        this.scanImageDir = scanImageDir;
    }
}
