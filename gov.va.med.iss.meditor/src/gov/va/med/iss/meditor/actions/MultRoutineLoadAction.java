/**
 * created 03/15/08
 */
package gov.va.med.iss.meditor.actions;

import gov.va.med.foundations.adapter.cci.VistaLinkConnection;
import gov.va.med.iss.meditor.utils.MEditorUtilities;
import gov.va.med.iss.meditor.utils.MultRoutineLoad;
import gov.va.med.iss.meditor.utils.RoutineNameDialogData;
import gov.va.med.iss.meditor.utils.RoutineNameDialogForm;
import gov.va.med.iss.connection.actions.VistaConnection;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
//import org.eclipse.swt.events.KeyEvent;
//import org.eclipse.swt.events.KeyListener;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * @author vhaisfiveyj
 *
 */
public class MultRoutineLoadAction implements IWorkbenchWindowActionDelegate {

	/**
	 * 
	 */
	public MultRoutineLoadAction() {
	}

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
		if (VistaConnection.getPrimaryServer()) {
			RoutineNameDialogForm dialogForm = new RoutineNameDialogForm(MEditorUtilities.getIWorkbenchWindow().getShell());
			RoutineNameDialogData data = dialogForm.openMultiple();
			if (data.getButtonResponse()) {
				String routineName = data.getTextResponse();
				String saveToDirectory = data.getDirectory();
				if (data.getUpperCase()) {
					routineName = routineName.toUpperCase();
				}
				if (! (routineName.compareTo("") == 0)) {
					//if (VistaConnection.getPrimaryServer()) {
						MultRoutineLoad.loadMultipleRoutines(routineName, saveToDirectory);
					//}
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


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
