package gov.va.mumps.debug.core.model;

import gov.va.mumps.debug.core.MDebugConstants;
import gov.va.mumps.debug.core.MDebugCorePlugin;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.LineBreakpoint;

public class MLineBreakpoint extends LineBreakpoint {

	/**
	 * Default constructor is required for the breakpoint manager
	 * to re-create persisted breakpoints. After instantiating a breakpoint,
	 * the <code>setMarker(...)</code> method is called to restore
	 * this breakpoint's attributes.
	 */
	public MLineBreakpoint() {
	}
	
	/**
	 * Constructs a line breakpoint on the given resource at the given
	 * line number. The line number is 1-based (i.e. the first line of a
	 * file is line number 1). The PDA VM uses 0-based line numbers,
	 * so this line number translation is done at breakpoint install time.
	 * 
	 * @param resource file on which to set the breakpoint
	 * @param lineNumber 1-based line number of the breakpoint
	 * @throws CoreException if unable to create the breakpoint
	 */
	public MLineBreakpoint(final IResource resource, final int lineNumber)
			throws CoreException {
		IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				//TODO: test that this is the correct ID. I suspect that the tutorial is wrong/bugged.
				//The api doc says it a needs the id of a an extension that is a resource marker.
				//the id for that one, strangely enough, is the plugin id (not model id) + its actual
				//extension id
				IMarker marker = resource
						.createMarker("gov.va.med.iss.debug.core.lineBreakpoint.marker");
				setMarker(marker);
				marker.setAttribute(IBreakpoint.ENABLED, Boolean.TRUE);
				marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
				marker.setAttribute(IBreakpoint.ID, getModelIdentifier());
				marker.setAttribute(IMarker.MESSAGE, "Line Breakpoint: "
						+ resource.getName() + " [line: " + lineNumber + "]");
			}
		};
		run(getMarkerRule(resource), runnable);
	}
	
	@Override
	public String getModelIdentifier() {
		return MDebugConstants.M_DEBUG_MODEL;
	}

}
