package gov.va.med.iss.meditor.actions;

import gov.va.med.iss.connection.actions.VistaConnection;
import gov.va.med.foundations.adapter.cci.VistaLinkConnection;
import gov.va.med.iss.meditor.utils.RoutineLoad;
//import gov.va.med.iss.meditor.utils.RoutineNameDialog;
import gov.va.med.iss.meditor.utils.MEditorUtilities;
import gov.va.med.iss.meditor.MEditorPlugin;
import gov.va.med.iss.meditor.utils.RoutineNameDialogForm;
import gov.va.med.iss.meditor.utils.RoutineNameDialogData;
//import gov.va.med.foundations.security.vistalink.DialogLogonForm;
//import gov.va.med.foundations.security.vistalink.DialogLogonData;
import java.lang.String;
//import java.util.Locale;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * Our sample action implements workbench action delegate.
 * The action proxy will be created by the workbench and
 * shown in the UI. When the user tries to use the action,
 * this delegate will be created and execution will be 
 * delegated to it.
 * @see IWorkbenchWindowActionDelegate
 */
public class RoutineEditAction implements IWorkbenchWindowActionDelegate {
//	private IWorkbenchWindow window;
	/**
	 * The constructor.
	 */
	public RoutineEditAction() {
	}
		
	public void run() {
	}
		
	public void run(IAction action) {
//		VistaConnection.setBadConnection(false);
		// JLI 110127 check that at least one server defined before showing dialog
		//if (VistaConnection.getPrimaryServer().compareTo(";;;") != 0) {
		if (VistaConnection.getPrimaryServer()) {
			VistaLinkConnection currentConnection = VistaConnection.getCurrentConnection();
			RoutineLoad.newPage = true;
			RoutineNameDialogForm dialogForm = new RoutineNameDialogForm(MEditorUtilities.getIWorkbenchWindow().getShell());
			RoutineNameDialogData data = dialogForm.open(true);
			if (data.getButtonResponse()) {
				String routineName = data.getTextResponse();
				boolean isReadOnly = data.getReadOnly();
				if (data.getUpperCase()) {
					routineName = routineName.toUpperCase();
				}
				if (routineName.length() > MEditorPlugin.P_SACC_MAX_LABEL_LENGTH) {  // SACC standard Maximum Label Length
					MessageDialog.openInformation(
							RoutineLoad.getWindow().getShell(),
							"Meditor Plug-in",
							// JLI 090918 - change Long Name problem to a warning
							//"Routine Name Exceeds Maximum Length of "+
							"Warning: Routine Name Exceeds Maximum Length of "+
							   MEditorPlugin.P_SACC_MAX_LABEL_LENGTH + 
							   " characters.\n\nIt will not be put in the "+
							   "Routine file");
				}
				//  JLI 090918 Don't Force failure if routine name is longer than SAC standard
				//  else if (! (routineName.compareTo("") == 0)) {
				if (! (routineName.compareTo("") == 0)) {
					//VistaConnection.getPrimaryServer(); //091029 - moved from RoutineLoad.routineLoad - to make check for change in servers
					if (VistaConnection.getPrimaryServer()) {
						RoutineLoad rl = new RoutineLoad();
						rl.loadRoutine(routineName, true, isReadOnly);
					}
				}
			}
		}
	}
	

	/**
	 * Selection in the workbench has been changed. We 
	 * can change the state of the 'real' action here
	 * if we want, but this can only happen after 
	 * the delegate has been created.
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

	/**
	 * We can use this method to dispose of any system
	 * resources we previously allocated.
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose() {
	}

	/**
	 * We will cache window object in order to
	 * be able to provide parent shell for the message dialog.
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	public void init(IWorkbenchWindow window) {
//		this.window = window;
	}
}