package us.pwc.vista.eclipse.tools.propertyPage;

public class NameFilter {
	private NameFilterType type;
	private String filter;
	
	public NameFilter(String value, NameFilterType type) {
		this.filter = value;
		this.type = type;
	}
	
	public String getValue() {
		return this.filter;
	}
	
	public NameFilterType getType() {
		return this.type;
	}
	
	public void setValue(String value) {
		this.filter = value;
	}
	
	public void setType(NameFilterType type) {
		this.type = type;
	}
	
	@Override
	public String toString() {
		return this.type.toString() + " " + this.filter;
	}
}
