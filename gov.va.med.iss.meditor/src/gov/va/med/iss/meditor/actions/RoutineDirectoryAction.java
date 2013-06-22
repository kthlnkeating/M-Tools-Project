/*
 * Created on Aug 24, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gov.va.med.iss.meditor.actions;

import gov.va.med.iss.connection.actions.VistaConnection;
import gov.va.med.iss.meditor.utils.MEditorUtilities;
import gov.va.med.iss.meditor.utils.RoutineDirectory;
import gov.va.med.iss.meditor.utils.RoutineNameDialogData;
import gov.va.med.iss.meditor.utils.RoutineNameDialogForm;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * @author vhaisfiveyj
 *
 */
public class RoutineDirectoryAction implements IWorkbenchWindowActionDelegate {

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
	 */
	public void dispose() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
	 */
	public void init(IWorkbenchWindow window) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		RoutineNameDialogForm dialogForm = new RoutineNameDialogForm(MEditorUtilities.getIWorkbenchWindow().getShell());
		RoutineNameDialogData data = dialogForm.open();
		if (data.getButtonResponse()) {
			String routineName = data.getTextResponse();
			if (data.getUpperCase()) {
				routineName = routineName.toUpperCase();
			}
			if (! (routineName.compareTo("") == 0)) {
				//VistaConnection.getPrimaryServer(); //091029 to make check for change in servers
				if (VistaConnection.getPrimaryServer()) {
					RoutineDirectory.getRoutineDirectory(routineName);
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub

	}

}
