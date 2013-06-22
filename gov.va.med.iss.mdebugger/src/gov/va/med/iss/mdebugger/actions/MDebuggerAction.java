package gov.va.med.iss.mdebugger.actions;

import gov.va.med.foundations.adapter.cci.VistaLinkConnection;
import gov.va.med.iss.connection.actions.VistaConnection;
import gov.va.med.iss.mdebugger.MDebuggerDialog;
import gov.va.med.iss.mdebugger.util.MDebuggerSteps;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

/**
 * Our sample action implements workbench action delegate.
 * The action proxy will be created by the workbench and
 * shown in the UI. When the user tries to use the action,
 * this delegate will be created and execution will be 
 * delegated to it.
 * @see IWorkbenchWindowActionDelegate
 */
public class MDebuggerAction implements IWorkbenchWindowActionDelegate {
	private IWorkbenchWindow window;
	
	private VistaLinkConnection myConnection = null;
	/**
	 * The constructor.
	 */
	public MDebuggerAction() {
	}

	/**
	 * The action has been activated. The argument of the
	 * method represents the 'real' action sitting
	 * in the workbench UI.
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action) {
		MDebuggerDialog mdialog = new MDebuggerDialog(window.getShell());
		String dbCommand = mdialog.open();
		if (! (dbCommand == "")) {
//			MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),"M Debugger",dbCommand);
			if (myConnection == null)
				myConnection = VistaConnection.getConnection();
			if (! (myConnection == null)) {
				IWorkbench workbench = PlatformUI.getWorkbench();
				try {
					workbench.showPerspective("gov.va.med.iss.mdebugger.perspective",workbench.getActiveWorkbenchWindow());
					MDebuggerSteps.startDebug(dbCommand,mdialog.getClearInits(),mdialog.initList);
				} catch (Exception e) {
					MessageDialog.openError(workbench.getActiveWorkbenchWindow().getShell(),"M Debugger","Error Loading perspective: "+e.getMessage());
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
		this.window = window;
	}
}