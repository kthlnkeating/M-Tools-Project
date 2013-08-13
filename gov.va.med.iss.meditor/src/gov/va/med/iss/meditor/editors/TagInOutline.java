package gov.va.med.iss.meditor.editors;

import org.eclipse.jface.text.Position;

import us.pwc.vista.eclipse.core.command.IMTagSupply;

public class TagInOutline extends OutlineContent implements IMTagSupply {
	public TagInOutline(String name, Position position) {
		super(name, position);
	}

	@Override
	public String getTag() {
		return super.toString();
	}
}
