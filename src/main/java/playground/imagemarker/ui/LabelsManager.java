package playground.imagemarker.ui;

import java.util.ArrayList;
import java.util.List;

public class LabelsManager {
	private static LabelsManager instance;
	private List<String> labels;
	private String lastSelectedLabel;
	
	private LabelsManager() {
		labels = new ArrayList<String>();
		labels.add("car");
		labels.add("tree");
		labels.add("apple");
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
}
