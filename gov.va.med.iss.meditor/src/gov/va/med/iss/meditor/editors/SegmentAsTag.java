package gov.va.med.iss.meditor.editors;

import org.eclipse.jface.text.Position;

public class SegmentAsTag {
	private String name;
	private Position position;
	private boolean tag;
	
	public SegmentAsTag(String name, Position position, boolean isTag) {
		this.name= name;
		this.position= position;
		this.tag = isTag;
	}

	public String toString() {
		return name;
	}
	
	public Position getPosition() {
		return position;
	}
	
	public boolean isTag() {
		return tag;
	}
}
