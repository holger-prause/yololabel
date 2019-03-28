package playground.imagemarker.ui;

import java.util.ArrayList;
import java.util.List;

public class LabelsManager {
	private static LabelsManager instance;
	private List<String> labels;
	private List<String> predefinedLabels;
    private String lastSelectedLabel;
	
	private LabelsManager() {
        predefinedLabels = new ArrayList<>();
        predefinedLabels.add("car");
        predefinedLabels.add("tree");
        predefinedLabels.add("apple");

        labels = new ArrayList<>();
        labels.addAll(predefinedLabels);
	}
	
	public static LabelsManager getInstance() {
		if(instance == null) {
			instance = new LabelsManager();
		}
		
		return instance;
	}

	public List<String> getLabels() {
		return labels;
	}

	public void setLabels(List<String> labels) {
		this.labels = labels;
	}

	public String getLastSelectedLabel() {
		return lastSelectedLabel;
	}

	public void setLastSelectedLabel(String lastSelectedLabel) {
		this.lastSelectedLabel = lastSelectedLabel;
	}

    public void resetToPredefinedLabels() {
        labels.clear();
        labels.addAll(predefinedLabels);
    }

    public void setPredefinedLabels(List<String> predefinedLabels) {
        this.predefinedLabels = predefinedLabels;
    }
}
