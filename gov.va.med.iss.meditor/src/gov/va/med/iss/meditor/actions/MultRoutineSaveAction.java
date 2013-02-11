package gov.va.med.iss.meditor.actions;

import gov.va.med.iss.connection.actions.VistaConnection;
import gov.va.med.iss.meditor.utils.MEditorUtilities;
import gov.va.med.iss.meditor.utils.MultRoutineSave;
import gov.va.med.iss.meditor.utils.RoutineNameDialogData;
import gov.va.med.iss.meditor.utils.RoutineNameDialogForm;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class MultRoutineSaveAction implements IWorkbenchWindowActionDelegate {

	/**
	 * 
	 */
	public MultRoutineSaveAction() {
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
			RoutineNameDialogData data = dialogForm.openMultSave();
			if (data.getButtonResponse()) {
				String routineName = data.getTextResponse();
				String loadFromDirectory = data.getDirectory();
				if (data.getUpperCase()) {
					routineName = routineName.toUpperCase();
				}
				if (! (routineName.compareTo("") == 0)) {
					MultRoutineSave.saveMultipleRoutines(loadFromDirectory, routineName);
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
