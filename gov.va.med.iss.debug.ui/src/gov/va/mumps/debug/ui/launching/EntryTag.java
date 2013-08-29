package gov.va.mumps.debug.ui.launching;

public class EntryTag {
	private String label;
	private String[] parameters;
	
	public EntryTag(String label, String[] parameters) {
		super();
		this.label = label;
		this.parameters = parameters;
	}

	public String getLabel() {
		return label;
	}

	public String[] getParameters() {
		return parameters;
	}
}
