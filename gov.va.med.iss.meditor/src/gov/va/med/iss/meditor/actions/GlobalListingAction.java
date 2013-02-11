/*
 * Created on Aug 24, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gov.va.med.iss.meditor.actions;

import gov.va.med.iss.meditor.utils.GlobalNameDialog;
import gov.va.med.iss.meditor.utils.GlobalListing;
//import gov.va.med.iss.meditor.editors.MEditor;
import gov.va.med.iss.connection.actions.VistaConnection;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * @author vhaisfiveyj
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GlobalListingAction implements IWorkbenchWindowActionDelegate {

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
//   JLI 071005 commented out the following line so GLOBAL LIST will work without having to have a routine open for editing
//		MEditor.currMEditor.resetLocation();
		GlobalNameDialog gnd = new GlobalNameDialog();
		String globalName = gnd.getGlobalName("GL");
		if (! (globalName.compareTo("-1") == 0)) {
			if (! (globalName.indexOf('(') == 0)) {
				//VistaConnection.getPrimaryServer(); //091027 to make check for change in servers
				if (VistaConnection.getPrimaryServer()) {
					GlobalListing.getGlobalListing(globalName,gnd.isCopy(),gnd.isDataOnly(),gnd.getSearchText(),gnd.isSearchDataOnly(),gnd.isSearchCaseSensitive());
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
