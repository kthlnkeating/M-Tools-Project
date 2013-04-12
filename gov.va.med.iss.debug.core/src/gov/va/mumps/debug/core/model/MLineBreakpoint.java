package gov.va.mumps.debug.core.model;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.ILineBreakpoint;

public class MLineBreakpoint extends AbstractMBreakpoint implements ILineBreakpoint {

	private String breakpointAsTag;
	
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
				IMarker marker = resource
						.createMarker("gov.va.med.iss.debug.core.lineBreakpoint.marker");
				setMarker(marker);
				marker.setAttribute(IBreakpoint.ENABLED, Boolean.TRUE);
				marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
				marker.setAttribute(IBreakpoint.ID, getModelIdentifier());
				marker.setAttribute(IMarker.MESSAGE, "Line Breakpoint: "
						+ resource.getName() + " [line: " + lineNumber + "]"); //this name shows up in the marker view, not in the breakpoint view
			}
		};
		run(getMarkerRule(resource), runnable);
	}
	
	@Override
	public int getLineNumber() throws CoreException {
		IMarker m = getMarker();
		if (m != null) {
			return m.getAttribute(IMarker.LINE_NUMBER, -1);
		}
		return -1;
	}

	@Override
	public int getCharStart() throws CoreException {
		IMarker m = getMarker();
		if (m != null) {
			return m.getAttribute(IMarker.CHAR_START, -1);
		}
		return -1;
	}

	@Override
	public int getCharEnd() throws CoreException {
		IMarker m = getMarker();
		if (m != null) {
			return m.getAttribute(IMarker.CHAR_END, -1);
		}
		return -1;
	}

	/*
	 * This just takes the routineName and transforms it into a very simple tag.
	 * This is more performant than opening the file and parsing for the most
	 * suitable tag location name given a line number. For example,
	 * file: MYROU.m and line 25 will be translated to this tag
	 * MYROU+24^MYROU
	 * 	
	 *  
	 */
	@Override
	public String getBreakpointAsTag() {
		
		if (breakpointAsTag != null)
			return breakpointAsTag;

		try {
			IMarker m = getMarker();
			if (m != null) {
				String fileName = m.getResource().getName();
				int lineLoc = getLineNumber();
				String rouName = fileName.substring(0, fileName.indexOf('.'));
				breakpointAsTag = rouName+ "+" +(lineLoc-1)+ "^" +rouName;
			}
		} catch (CoreException e) {
		}
		
		return breakpointAsTag;
	}

}
