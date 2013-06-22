package gov.va.mumps.debug.core.model;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IBreakpoint;

public class MTagBreakpoint extends AbstractMBreakpoint {
	
	private final String TAG = "gov.va.mumps.debug.core.model.MTag";
	
	public MTagBreakpoint() {
	}
	
	public MTagBreakpoint(final String tag, final IResource resource) throws DebugException {
		IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				IMarker marker = resource
						.createMarker("gov.va.med.iss.debug.core.tagBreakpoint.marker");
				setMarker(marker);
				marker.setAttribute(IBreakpoint.ENABLED, Boolean.TRUE);
				marker.setAttribute(IBreakpoint.ID, getModelIdentifier());
				marker.setAttribute(TAG, tag);
				marker.setAttribute(IMarker.MESSAGE, tag);
			}
		};
		run(getMarkerRule(resource), runnable);
	}

	@Override
	public String getBreakpointAsTag() {
		
		IMarker m = getMarker();
		if (m != null) {
			return m.getAttribute(TAG, null);
		}
		
		return null;
	}

}
