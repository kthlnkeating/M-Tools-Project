package gov.va.med.iss.meditor.editors;

import org.eclipse.jface.text.Position;

public class OutlineContent {
	private String name;
	private Position position;
	
	public OutlineContent(String name, Position position) {
		this.name= name;
		this.position= position;
	}

	@Override
	public String toString() {
		return name;
	}
	
	public Position getPosition() {
		return position;
	}
}
