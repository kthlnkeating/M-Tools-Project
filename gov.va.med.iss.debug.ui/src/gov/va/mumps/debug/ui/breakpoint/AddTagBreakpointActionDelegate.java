package gov.va.mumps.debug.ui.breakpoint;

import gov.va.mumps.debug.core.model.MTagBreakpoint;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

public class AddTagBreakpointActionDelegate implements IViewActionDelegate {
	
	private IViewPart viewPart;
	
	@Override
	public void run(IAction action) { //TODO: not sure why eclipse wants this to be an IActionDelegate / not sure what to do with action parm

		MTagDialog dialog = new MTagDialog(viewPart.getViewSite().getShell());
		dialog.open();
		String tag = dialog.getTag();
		
		if (tag == null || tag.isEmpty())
			return;
		
		try {
			IResource res = ResourcesPlugin.getWorkspace().getRoot();
			//TODO: may be better to find the resource of a file based on the routine in the tag? but which if there are more than 1 project...
			DebugPlugin.getDefault().getBreakpointManager().addBreakpoint(new MTagBreakpoint(
					tag,
					res));
		} catch (CoreException e) {
			
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
	}

	@Override
	public void init(IViewPart viewPart) {
		this.viewPart = viewPart;
	}
	
}
