package gov.va.mumps.debug.core.model;

import gov.va.mumps.debug.core.MDebugConstants;

import org.eclipse.debug.core.model.Breakpoint;

public abstract class AbstractMBreakpoint extends Breakpoint {

//	private String breakpointAsTag;
	
	@Override
	public String getModelIdentifier() {
		return MDebugConstants.M_DEBUG_MODEL;
	}
	
	abstract String getBreakpointAsTag();
	
//	public String getBreakpointAsTag() {
//		//if null, auto discover its value. this breakpoint could have been restored from a previous session
//		//TODO: put this in an IWorkspaceRunnable
//		return breakpointAsTag;
//	}

}
