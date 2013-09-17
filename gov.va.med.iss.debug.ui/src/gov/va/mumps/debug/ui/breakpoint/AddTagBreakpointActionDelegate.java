package gov.va.mumps.debug.ui.breakpoint;

import gov.va.mumps.debug.core.model.MTagBreakpoint;
import gov.va.mumps.debug.ui.MDebugUIPlugin;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.statushandlers.StatusManager;

import us.pwc.vista.eclipse.core.validator.ICommonRegexs;
import us.pwc.vista.eclipse.core.validator.RegexInputValidator;

public class AddTagBreakpointActionDelegate implements IViewActionDelegate {
	
	private IViewPart viewPart;
	
	@Override
	public void run(IAction action) { //not sure why eclipse wants this to be an IActionDelegate / not sure what to do with action parm
		Shell shell = this.viewPart.getViewSite().getShell();
		IInputValidator validator = new RegexInputValidator(true, ICommonRegexs.M_CODE_LOCATION, "code location");
		String title = "Code Location";
		InputDialog dialog = new InputDialog(shell, title, "Please enter a code location (label+offset^routine):", "", validator);
		try {
			if (InputDialog.OK == dialog.open()) {
				String result = dialog.getValue();
				//EntryTag mcl = EntryTag.getInstance(result);
				IResource res = ResourcesPlugin.getWorkspace().getRoot();
				DebugPlugin.getDefault().getBreakpointManager().addBreakpoint(new MTagBreakpoint(result, res));
			}
		} catch (CoreException coreException) {
			StatusManager.getManager().handle(coreException, MDebugUIPlugin.PLUGIN_ID);
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
