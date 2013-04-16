package gov.va.mumps.debug.ui.breakpoint;

import gov.va.mumps.debug.core.model.MLineBreakpoint;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.source.IVerticalRulerInfo;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

public class ToggleMBreakpointAction extends Action {

	private ITextEditor editor;
	private IVerticalRulerInfo rulerInfo;
	
	@Override
	public void run() {
		IBreakpointManager manager = DebugPlugin.getDefault().getBreakpointManager();
		IBreakpoint[] breakpoints = manager.getBreakpoints();
		IResource resource = getResource();
		int lineNumber = rulerInfo.getLineOfLastMouseButtonActivity() + 1;
		for (int i = 0; i < breakpoints.length; i++) {
			IBreakpoint bp = breakpoints[i];
			if (bp instanceof MLineBreakpoint) {
				MLineBreakpoint breakpoint = (MLineBreakpoint)bp;
				if (breakpoint.getMarker().getResource().equals(resource)) {
					try {
						if (breakpoint.getLineNumber() == lineNumber) {
							// remove
							breakpoint.delete();
							return;
						}
					} catch (CoreException e) {
						e.printStackTrace();
					}
				}
			}
		}
		createBreakpoint();
	}
	
	protected IResource getResource() {
		IEditorInput input= editor.getEditorInput();
		IResource resource= (IResource) input.getAdapter(IFile.class);
		if (resource == null) {
			resource= (IResource) input.getAdapter(IResource.class);
		}
		return resource;
	}
	
	protected void createBreakpoint() {
		IResource resource = getResource();
		int lineNumber = rulerInfo.getLineOfLastMouseButtonActivity();
		try {
			MLineBreakpoint lineBreakpoint = new MLineBreakpoint(resource, lineNumber + 1);
			DebugPlugin.getDefault().getBreakpointManager().addBreakpoint(lineBreakpoint);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	
	public ToggleMBreakpointAction(ITextEditor editor,
			IVerticalRulerInfo rulerInfo) {
		super("Toggle Line Breakpoint");
		this.editor = editor;
		this.rulerInfo = rulerInfo;
	}

}
