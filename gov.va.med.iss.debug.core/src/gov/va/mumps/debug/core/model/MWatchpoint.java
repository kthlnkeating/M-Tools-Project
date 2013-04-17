package gov.va.mumps.debug.core.model;

import gov.va.mumps.debug.core.MDebugConstants;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.Breakpoint;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IWatchpoint;

public class MWatchpoint extends Breakpoint implements IWatchpoint {

	private final String WATCHPOINT = "gov.va.mumps.debug.core.model.Watchpoint";
	
	public MWatchpoint() {
	}
	
	public MWatchpoint(final String watchpoint, final IResource resource) throws DebugException {
		IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				IMarker marker = resource
						.createMarker("gov.va.med.iss.debug.core.watchpoint.marker"); //TODO: need a good system for looking up constants
				setMarker(marker);
				marker.setAttribute(IBreakpoint.ENABLED, Boolean.TRUE);
				marker.setAttribute(IBreakpoint.ID, getModelIdentifier());
				marker.setAttribute(WATCHPOINT, watchpoint);
				marker.setAttribute(IMarker.MESSAGE, watchpoint);
			}
		};
		run(getMarkerRule(resource), runnable);
	}

	
	@Override
	public String getModelIdentifier() {
		return MDebugConstants.M_DEBUG_MODEL;
	}

	@Override
	public boolean isAccess() throws CoreException {
		return false; //does not suspend when the watched variable is read from
	}

	@Override
	public boolean isModification() throws CoreException {
		return true; //it always suspends when modified
	}

	@Override
	public void setAccess(boolean arg0) throws CoreException {
		//not possible to change this behavior
	}

	@Override
	public void setModification(boolean arg0) throws CoreException {
		//not possible to change this behavior
	}

	@Override
	public boolean supportsAccess() {
		return false; //does not support read access suspending
	}

	@Override
	public boolean supportsModification() {
		return true; //always supports variable changed suspending
	}
	
	public String getWatchpointVariable() {
		
		IMarker m = getMarker();
		if (m != null) {
			return m.getAttribute(WATCHPOINT, null);
		}
		
		return null;
	}

}
