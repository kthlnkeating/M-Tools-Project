package gov.va.mumps.debug.core.model;

import gov.va.mumps.debug.core.MDebugConstants;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.Breakpoint;

public abstract class AbstractMBreakpoint extends Breakpoint {
	@Override
	public String getModelIdentifier() {
		return MDebugConstants.M_DEBUG_MODEL;
	}
	
	public abstract String getBreakpointAsTag();
	
	public abstract String getAsDollarTextInput() throws CoreException;
}
