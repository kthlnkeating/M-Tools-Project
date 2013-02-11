/*
 * Created on Aug 10, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gov.va.med.iss.connection.actions;

import gov.va.med.foundations.adapter.cci.VistaLinkConnection;
import gov.va.med.foundations.security.vistalink.EclipseConnection;
import gov.va.med.foundations.security.vistalink.VistaKernelPrincipalImpl;
import gov.va.med.iss.connection.VLConnectionPlugin;
import gov.va.med.iss.connection.preferences.ConnectionPreferencePage;

import javax.security.auth.login.LoginContext;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * @author vhaisfiveyj
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class VistaDisconnect  implements IWorkbenchWindowActionDelegate {
	public IWorkbenchWindow window;
	public static IWorkbenchWindow windowx;
	/**
	 * The constructor.
	 */
	
	private static final Logger logger = Logger.getLogger(VistaConnection.class);

	public VistaDisconnect() {
	}
	

	/**
	 * The action has been activated. The argument of the
	 * method represents the 'real' action sitting
	 * in the workbench UI.
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run() {
		VistaConnection.disconnect();
	}
	public void run(IAction action) {
		run();
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
		windowx = window;
		this.window = window;
	}
}
