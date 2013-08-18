/*
 * Created on Mar 21, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gov.va.med.foundations.security.vistalink;

import gov.va.med.iss.connection.actions.VistaConnection;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginException;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;


//import gov.va.med.iss.connection.actions.LaunchPoint;


/**
 * @author vhaisfiveyj
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class EclipseConnection {
	
	@SuppressWarnings("unused")
	private static Logger logger;
	private Frame topFrame;
	private VistaKernelPrincipalImpl userPrincipal;
	private EclipseLoginModule eclipseLoginModule;
	
	@SuppressWarnings("rawtypes")
	public VistaKernelPrincipalImpl getConnection(String server, String port) {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		Shell shell = window.getShell();
		
		Composite composite = new Composite(shell, SWT.EMBEDDED);
		composite.setBounds(20, 20, 300, 200);
		composite.setLayout(new RowLayout( ));
		topFrame = SWT_AWT.new_Frame(composite);
		
		// create the callback handler
		CallbackHandlerSwing cbhSwing = new CallbackHandlerSwing(topFrame);
		
		try {
			
			CurrentIpAndPort currentIpAndPort = new CurrentIpAndPort();
			currentIpAndPort.setIp(server);
			currentIpAndPort.setPort(port);
			Subject subject = new Subject();
			Map sharedState = new HashMap();

			AppConfigurationEntry loginAppConfig = getAppConfigurationEntry(currentIpAndPort.getIp(), currentIpAndPort.getPort());
			VistaLinkJaasConfiguration loginConfiguration = new VistaLinkJaasConfiguration(loginAppConfig);
			Configuration.setConfiguration(loginConfiguration);

			try {
				eclipseLoginModule = new EclipseLoginModule();
			} catch (Exception e) {
				throw new RuntimeException("Error in eclipseLoginModule creation "+e.getMessage());
			}
			try {
				eclipseLoginModule.initialize(subject, cbhSwing, sharedState, optionMap);
			} catch (Exception e) {
				throw new RuntimeException("Error in eclipseLoginModule.initialize "+e.getMessage());

			}
			@SuppressWarnings("unused")
			boolean loggedin = eclipseLoginModule.login();
			@SuppressWarnings("unused")
			boolean committed = eclipseLoginModule.commit();
	
		// get principal
		userPrincipal = VistaKernelPrincipalImpl.getKernelPrincipal(eclipseLoginModule.getSubject());
		} catch (Exception e) {
			VistaConnection.setBadConnection(true);
			if (e.getMessage().indexOf("Connection timed out") > -1) {
				MessageDialog.openInformation(
						window.getShell(),
						"Meditor Login Error",
						"Timed out while attempting to connect.\n\nCheck that  \""+server+"\" and "+port+" are correct for server and port.\n\nCheck that the listener is running.");

			}
			else if (e.getMessage().indexOf("Connection refused: connect") > -1) {
				MessageDialog.openError(
						window.getShell(),
						"MEditor Login Error",
						"Connection Refused - check if VistALink listener is running");
			}
			else if (e.getMessage().indexOf("response is not VistaLink") > -1) {
				MessageDialog.openError(
						window.getShell(),
						"MEditor Login Error",
						"Connection Refused - does not appear to be a VistALink listener (a VistaLink not RPCBroker listener port is needed).");
			}
			else if (e.getMessage().indexOf("ArrayIndexOutOfBounds") > -1) {
				MessageDialog.openError(
						window.getShell(),
						"MEditor Login Error",
						"Connection Refused - check that a VistALink listener is running on the specified port and that the port number is not for an RPCBroker listener.");
			}
			else if (e.getMessage().indexOf("Error converting port string to integer") > -1) {
				MessageDialog.openError(
						window.getShell(),
						"MEditor Login Error",
						"Connection Refused - not a numeric port number (and perhaps other problems).  From the menu select Window - Preferences - expand the VistA tab and select Connection.  Add a new entry with the correct data, then use the  Move Up button to make it first in the list, then select the bad entry (next to the top) and click the Remove button to remove it.  Then click Apply and OK.");
			}
			else {
				MessageDialog.openInformation(
					window.getShell(),
					"Meditor Login Error",
					e.getMessage());
			}
			userPrincipal = null;
			topFrame.dispose();
		}
		if (! (topFrame == null)) topFrame.dispose();
		return userPrincipal;
	}

	
	@SuppressWarnings("rawtypes")
	Map optionMap = new HashMap();
	
	@SuppressWarnings("unchecked")
	private AppConfigurationEntry getAppConfigurationEntry(String ip, String port) {

		// Map optionMap = new HashMap();
		optionMap.put(gov.va.med.foundations.security.vistalink.VistaLoginModule.SERVER_ADDRESS_KEY, ip);
		optionMap.put(gov.va.med.foundations.security.vistalink.VistaLoginModule.SERVER_PORT_KEY, port);

		AppConfigurationEntry myEntry = new AppConfigurationEntry("gov.va.med.foundations.security.vistalink.EclipseLoginModule",
				AppConfigurationEntry.LoginModuleControlFlag.REQUISITE, optionMap);
		
		return myEntry;
	}
	
	/**
	 * Do the logout
	 */

	public void logout() {

		// Kernel logout
		if (this.userPrincipal != null) {

			try {

				eclipseLoginModule.logout();

			} catch (LoginException e) {

			}
/*
			statusTextField.setText(STATUS_DISCONNECTED_TEXT);
			statusTextField.getAccessibleContext().firePropertyChange(
				"VISIBLE_PROPERTY_CHANGE",
				STATUS_CONNECTED_TEXT,
				STATUS_DISCONNECTED_TEXT);
			statusTextField.getAccessibleContext().setAccessibleName(STATUS_LABEL_TEXT + STATUS_DISCONNECTED_TEXT);
*/
//			disconnectedControlsEnable();
			userPrincipal = null;
		}
//		this.timeout = DEFAULT_TIMEOUT;

	}


	/**
	 * create all components for the window
	 * @return Component
	 */
	@SuppressWarnings("unused")
	private Component createComponents() {

		try {
			// create textarea/textfield borders with Java look and feel (haven't set system L&F yet)
			createFocusBorders();
		} catch (Exception e) {
/*
			DialogConfirm.showDialogConfirm(
				topFrame,
				e.getMessage(),
				"Error",
				DialogConfirm.INFORMATION_MESSAGE,
				this.timeout);
*/
		}

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
/*
		//create Panel for Connection controls
		mainPanel.add(createConnectionPanel(), BorderLayout.NORTH);

		//create Panel for RPC tabs
		this.tabPane = (JTabbedPane) createTabPanel();
		mainPanel.add(this.tabPane, BorderLayout.CENTER);

		//create Panel for RPC counter
//		mainPanel.add(createRpcCounterPane(), BorderLayout.SOUTH);

		// set controls to "not logged on" state
		disconnectedControlsEnable();

		connectedFocusTraversalPolicy = new ConnectedFocusTraversalPolicy();
		disconnectedFocusTraversalPolicy = new DisconnectedFocusTraversalPolicy();
		mainPanel.setFocusCycleRoot(true);
		mainPanel.setFocusTraversalPolicy(disconnectedFocusTraversalPolicy);
*/
		return mainPanel;

	}


	private void createFocusBorders()
	throws UnsupportedLookAndFeelException, IllegalAccessException, ClassNotFoundException, InstantiationException {

	// set the look and feel to java.
	UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());

	// get border from current LaF
	@SuppressWarnings("unused")
	Border defaultBorder = UIManager.getBorder("TextField.border");
/*
	focusBorder =
		BorderFactory.createCompoundBorder(UIManager.getBorder("List.focusCellHighlightBorder"), defaultBorder);
	noFocusBorder =
		BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(UIManager.getColor("control"), 1),
			defaultBorder);
*/
}

	/**
	 * We can use this method to dispose of any system
	 * resources we previously allocated.
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose() {
	}

}
