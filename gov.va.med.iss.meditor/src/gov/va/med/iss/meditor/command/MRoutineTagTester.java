package gov.va.med.iss.meditor.command;

import gov.va.med.iss.meditor.editors.SegmentAsTag;

import org.eclipse.core.expressions.PropertyTester;

public class MRoutineTagTester extends PropertyTester {
	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (receiver instanceof SegmentAsTag) {
			return ((SegmentAsTag) receiver).isTag();
		} else {			
			return false;
		}
	}
}
