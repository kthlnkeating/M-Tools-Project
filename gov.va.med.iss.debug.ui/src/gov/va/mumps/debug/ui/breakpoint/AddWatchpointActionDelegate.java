package gov.va.mumps.debug.ui.breakpoint;

import gov.va.mumps.debug.core.model.MWatchpoint;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

public class AddWatchpointActionDelegate implements IViewActionDelegate {
	
	private IViewPart viewPart;

	@Override
	public void run(IAction action) {
		MWatchpointDialog dialog = new MWatchpointDialog(viewPart.getViewSite().getShell());
		dialog.open();
		String watchpoint = dialog.getWatchpoint();
		
		if (watchpoint == null || watchpoint.isEmpty())
			return;
		
		try {
			IResource res = ResourcesPlugin.getWorkspace().getRoot();
			DebugPlugin.getDefault().getBreakpointManager().addBreakpoint(new MWatchpoint(
					watchpoint,
					res));
		} catch (CoreException e) {
		}
	}

	@Override
	public void selectionChanged(IAction arg0, ISelection arg1) {
	}

	@Override
	public void init(IViewPart viewPart) {
		this.viewPart = viewPart;
	}
}
