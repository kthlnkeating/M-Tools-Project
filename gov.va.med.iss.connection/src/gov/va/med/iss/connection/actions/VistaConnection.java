package gov.va.med.iss.connection.actions;

import gov.va.med.foundations.adapter.cci.VistaLinkConnection;
import gov.va.med.foundations.rpc.RpcRequest;
import gov.va.med.foundations.rpc.RpcRequestFactory;
import gov.va.med.foundations.rpc.RpcResponse;
import gov.va.med.foundations.security.vistalink.EclipseConnection;
import gov.va.med.foundations.security.vistalink.VistaKernelPrincipalImpl;
import gov.va.med.foundations.utilities.FoundationsException;
import gov.va.med.iss.connection.VLConnectionPlugin;
import gov.va.med.iss.connection.dialogs.SecondaryServerSelectionDialogData;
import gov.va.med.iss.connection.dialogs.SecondaryServerSelectionDialogForm;
import gov.va.med.iss.connection.preferences.ConnectionPreferencePage;
import gov.va.med.iss.connection.preferences.ServerConnectionData;
import gov.va.med.iss.connection.utilities.MPiece;

import java.util.ArrayList;

import org.apache.log4j.BasicConfigurator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
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
public class VistaConnection {
	public IWorkbenchWindow window;
	public static IWorkbenchWindow windowx;
	/**
	 * The constructor.
	 */
	
	private static EclipseConnection eclipseConnection = null;
	private static VistaKernelPrincipalImpl userPrincipal = null;
	private static VistaKernelPrincipalImpl primaryPrincipal = null;
	@SuppressWarnings("rawtypes")
	private static ArrayList connectionList = new ArrayList(20);
	
	private static ServerConnectionData primaryData = new ServerConnectionData();
	private static VistaLinkConnection primaryConnection = null;
    
    private static ServerConnectionData currentData = new ServerConnectionData();
    private static VistaLinkConnection currConnection = null;
    //
	private static String[] checkedServers = null;

    private static ServerConnectionData defaultData = new ServerConnectionData();
    
    private static boolean shownNoServerDefined = false;
    private static boolean badConnection = false;


	public VistaConnection() {
	}
	
	public VistaConnection(ServerConnectionData data) {
        currentData = new ServerConnectionData(data);
    }
	
	public static void run(IWorkbenchWindow window) {
		VistaConnection vistaConnection = new VistaConnection();
		vistaConnection.window = window;
		windowx = window;
		vistaConnection.run();
	}
	
	/***
	 * Used without arguments to get the primary connection
	 * 
	 * @return an instance of the VistaLinkConnection for the 
	 * connection to the primary server
	 */
	public static VistaLinkConnection getConnection() {
		if (! VistaConnection.getPrimaryServer()) {
			return null;
		}
		else {
			if (primaryConnection == null) {
				VistaConnection vistaConnection = new VistaConnection();
				vistaConnection.run();
				vistaConnection = null;
				if (currConnection == null) {
					return null;
				}
				setPrimaryServer(); // #2
				try {
					if (VistaConnection.checkForProductionAccount(VistaConnection.getConnection())) {
						MessageDialog.openWarning(
								PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
								"Meditor Plug-in Routine Load - PRODUCTION account",
								"** WARNING **\n\n"+
								MPiece.getPiece(VistaConnection.getCurrentServer(),";",2,3)+" is a PRODUCTION account.\n\n" +
								"You should use extreme caution in editing and saving a " +
								"routine back into a PRODUCTION account.");
					}
				} catch (Exception e) {
					MessageDialog.openError(
							PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
							"Meditor Plug-in Routine Load Error",
							"An error occured while checking for whether the current\n" +
							"server "+VistaConnection.getCurrentConnection()+" is a\n"+
							"production server or not.");
				}
			}
			else {  // reset current value to show primary server
				currentData = new ServerConnectionData(primaryData);
	            
				currConnection = primaryConnection;
				userPrincipal = primaryPrincipal;
			}
			// 080523 code added to make sure connection is good, in case of
			// loss of pre-existing connection
			if (primaryConnection != null) {
				// 080523 added functionality to check for loss of connection
				// if lost, it notifies user to sign on again, and removes
				// current connection
				// 091029 if (! checkConnection(primaryConnection, primaryServerName, primaryPort, primaryServerAddress)) {
		        if (! checkConnection(primaryConnection, primaryData.port, primaryData.serverAddress)) {
					primaryConnection = null;
					primaryConnection = getConnection();
				}
			}
			return primaryConnection;
		}
	}
	
	// 080523 added functionality to check for loss of connection
	// if lost, it notifies user to sign on again, and removes
	// current connection
	
	// JLI 100226 - connection should only depend on port and url, 
	// removed other factors (name and projectname) 
	private static boolean checkConnection(VistaLinkConnection connection,
			String port, String url) {
		if (connection == null)
			return false;
		RpcRequest vReq = null;
		boolean result = true;
		try {
			vReq = RpcRequestFactory.getRpcRequest("", "XWB GET BROKER INFO");
		} catch (FoundationsException fe) {
			result = false;
		}
		if (result) {
			vReq.setUseProprietaryMessageFormat(false);
			try {
				@SuppressWarnings("unused")
				RpcResponse vResp = connection.executeRPC(vReq);
			} catch (Exception e) {
				result = false;
			}
		}
		if (! result) {
			IWorkbench wb = PlatformUI.getWorkbench();
			IWorkbenchWindow window = wb.getActiveWorkbenchWindow();
			MessageDialog.openInformation(
					window.getShell(),
					"VistA Connection Error",
					"The connection to the server has been lost, sign in again");
            ConnectionData connData = getMatchingConnection(port, url); // JLI 100226 changed for change in method, since connection depends only on port and url
			removeConnection(connData);
		}
		return result;
	}
	
    @SuppressWarnings("unchecked")
	public static VistaLinkConnection getConnection(ServerConnectionData data) {
		currConnection = null;
		for (int i=0; i<connectionList.size(); i++) {
			if (! (connectionList.get(i) == null) ) {
				ConnectionData connData = (ConnectionData)connectionList.get(i);
				if ((connData.getServerAddress().compareTo(data.serverAddress) == 0) &&
						connData.getServerPort().compareTo(data.port) == 0) {
					// 080523 added functionality to check for loss of connection
					// if lost, it notifies user to sign on again, and removes
					// current connection
					setServer(connData);
					if (! checkConnection(currConnection, data.port, data.serverAddress)) {
						currConnection = null;
					}
					if (currConnection != null)
						return currConnection;
				}
			}
		}
		VistaConnection vistaConnection = new VistaConnection(data);
		vistaConnection.run();
		// only add connection if connection has been made
		if (! (currConnection == null)) {
			currentData = new ServerConnectionData(data);
			
			ConnectionData connData = new ConnectionData();
			connData.setServerAddress(currentData.serverAddress);
			connData.setServerPort(currentData.port);
			connData.setServerName(currentData.serverName);
			connData.setConnection(currConnection);
			connData.setPrincipal(userPrincipal);
			connData.setEclipseConnection(eclipseConnection);
			connectionList.add(connData);
			if (primaryConnection == null) {
				primaryConnection = currConnection;
				primaryPrincipal = userPrincipal;
				
				primaryData = new ServerConnectionData(data);
			}
		}
		return currConnection;
	}
	
	public static VistaKernelPrincipalImpl getPrincipal() {
		if (userPrincipal == null) {
			VistaConnection vistaConnection = new VistaConnection();
			vistaConnection.run();
			vistaConnection = null;
		}
		return userPrincipal;
	}
	
	@SuppressWarnings("rawtypes")
	public static void disconnect() {
 		if (connectionList.size() == 0) {
 			// nothing to disconnect
 		}
 		else if (connectionList.size() == 1) {
			ConnectionData connData = (ConnectionData)connectionList.get(0);
			removeConnection(connData);
		}
		else if (PlatformUI.getWorkbench().isClosing()){  // Workbench closing
			for (int i=0; i<connectionList.size(); i++) {
				if (connectionList.get(i) != null) {
					ConnectionData connData = (ConnectionData)connectionList.get(i);
					removeConnection(connData);
				}
			}
		}
		else {	// user closing selected connections
			ArrayList serverList = ConnectionPreferencePage.getServerList();
			if (serverList.size() > 1) {
				SecondaryServerSelectionDialogData.setTotalList(serverList);
				String[] servers = SecondaryServerSelectionDialogData.getAllServers();
				int totCount = 0;
				for (int i=0; i<servers.length; i++) {
					@SuppressWarnings("unused")
					String name = MPiece.getPiece(servers[i],";",2);
					String port = MPiece.getPiece(servers[i],";",4);
					String url = MPiece.getPiece(servers[i],";",3);
                    @SuppressWarnings("unused")
					String project = MPiece.getPiece(servers[i],";",5);
                    ConnectionData connData = getMatchingConnection(port,url); // JLI 100226 changed for change in method, since connection depends only on port and url
					if (! (connData == null)) {
						totCount++;
					}
				}
				String[] activeServers = new String[totCount];
				totCount = 0;
				for (int i=0; i<servers.length; i++) {
					@SuppressWarnings("unused")
					String name = MPiece.getPiece(servers[i],";",2);
					String port = MPiece.getPiece(servers[i],";",4);
					String url = MPiece.getPiece(servers[i],";",3);
                    @SuppressWarnings("unused")
					String project = MPiece.getPiece(servers[i],";",5); // JLI 090908 added for Source Code Version Control
                    ConnectionData connData = getMatchingConnection(port,url); // JLI 100226
					if (! (connData == null)) {
						activeServers[totCount++] = servers[i];
					}
				}
				SecondaryServerSelectionDialogForm secondary = new SecondaryServerSelectionDialogForm(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),SWT.NONE);
				SecondaryServerSelectionDialogData data = secondary.open("DISCONNECT",activeServers);
				checkedServers = data.getCheckedList();
				for (int i=0; i<checkedServers.length; i++) {
					@SuppressWarnings("unused")
					String name = MPiece.getPiece(checkedServers[i],";",1);
					String port = MPiece.getPiece(checkedServers[i],";",3);
					String url = MPiece.getPiece(checkedServers[i],";",2);
                    @SuppressWarnings("unused")
					String project = MPiece.getPiece(checkedServers[i],";",4); // JLI 090908 added for Source Code Version Control
                    ConnectionData connData = getMatchingConnection(port,url); // JLI 110914
					if (! (connData == null)) {
						removeConnection(connData);
					}
				}
			}
		}
	}
	
	static private void removeConnection(ConnectionData connData) {
		EclipseConnection econnect = connData.getEclipseConnection();
		econnect.logout();
		@SuppressWarnings("unused")
		VistaKernelPrincipalImpl principal = connData.getPrincipal();
		@SuppressWarnings("unused")
		VistaLinkConnection vlConnection = connData.getConnection();
		econnect = null;
		principal = null;
		vlConnection = null;
		if ((currentData.port.compareTo(connData.getServerPort()) == 0) &&
				(currentData.serverAddress.compareTo(connData.getServerAddress()) == 0)){
			eclipseConnection = null;
			userPrincipal = null;
			currConnection = null;
			currentData.reset();
		}
		if ((primaryData.port.compareTo(connData.getServerPort()) == 0) &&
				(primaryData.serverAddress.compareToIgnoreCase(connData.getServerAddress()) == 0)){
			primaryPrincipal = null;
			primaryConnection = null;
			primaryData.reset();
		}
		connectionList.remove(connData);
	}

	/**
	 * The action has been activated. The argument of the
	 * method represents the 'real' action sitting
	 * in the workbench UI.
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run() {
		if (! (window == null)) {
			windowx = window;
		}
		else if (! (windowx == null)) {
			window = windowx;
		}
		IWorkbench wb = PlatformUI.getWorkbench();
		window = wb.getActiveWorkbenchWindow();
		if (window == null) {
			MessageDialog.openInformation(
					window.getShell(),
					"VistA Connection Error",
					"window not defined in run()");
		}
		if (! (currConnection == null)) {
/*
			MessageDialog.openInformation(
					window.getShell(),
					"Vista Connection",
					"Already connected to Server: "+currServerAddress+" at Port: "+currPort);
*/
		}
		else if (currentData.serverAddress.compareTo("") == 0) {
			try {
				getDefaultPrefs();
				clearShownNoServerDefined();
				if ((currentData.serverAddress.compareTo("") == 0) || (currentData.port.compareTo("") == 0)) {
					// JLI 110127 extracted to method
					ShowConnectionServerMsg();
				}
			} catch (Exception e) {
			
			}
		}
		if ((currentData.serverAddress.compareTo("") != 0) && (getMatchingConnection(currentData.port, currentData.serverAddress) == null)){
//				PropertyConfigurator.configure("log4jConfig.properties");
//				DOMConfigurator.configure("%ECLIPSEHOME%/plugins/gov.va.med.iss.connection_0.9.0/log4jConfig.xml");
			BasicConfigurator.configure();
//				logger.fatal("output from logger.fatal");
//				System.out.println("Output from println in VistaConnection");
		
			try {
				eclipseConnection = new EclipseConnection();
			} catch (Exception e) {
				eclipseConnection = null;
				MessageDialog.openInformation(
						window.getShell(),
						"VistA Connection Error",
						"Error in EclipseConnection creation "+e.getMessage());
				System.out.println("Error in EclipseConnection creation: "+e.getMessage());
			}
			if (!(eclipseConnection == null)) {
				try {  // The following line gets the connection to the server
					userPrincipal = eclipseConnection.getConnection(currentData.serverAddress, currentData.port, window);
				} catch (Exception e) {
					MessageDialog.openInformation(
							window.getShell(),
							"VistA Connection Error",
							"Error in eclipseConnection.getConnection "+e.getMessage());
					System.out.println("Error in eclipseConnection.getConnection: "+e.getMessage());
					userPrincipal = null;
				}
				if (!(userPrincipal == null)) {
					try {
						currConnection = userPrincipal.getAuthenticatedConnection();
					} catch (Exception e) {
						MessageDialog.openInformation(
								window.getShell(),
								"VistA Connection Error",
								"Error in userPrincipal.getAuthenicatedConnection()"+e.getMessage());
						System.out.println("Error in userPrincipal.getAuthen...: "+e.getMessage());
					}
				}
			}
		}
	}
	
	static public void getDefaultPrefs() throws Exception {
		IPreferencesService prefService = Platform.getPreferencesService();
		defaultData.serverName = prefService.getString(VLConnectionPlugin.PLUGIN_ID, ConnectionPreferencePage.P_SERVER_NAME, "", null);
		// JLI 110127 if default name is empty, show message and then exit
		if (defaultData.serverName == "") {
			ShowConnectionServerMsg();
		}
		currentData.serverName = defaultData.serverName;
		if (primaryData.serverName.compareTo("") != 0) {
			if (primaryData.serverName.compareTo(currentData.serverName) != 0) {
	            IWorkbench wb = PlatformUI.getWorkbench();
	            IWorkbenchWindow window = wb.getActiveWorkbenchWindow();
	            MessageDialog.openInformation(
	                    window.getShell(),
	                    "VistA Connection Change",
	                    "Switching Primary Servers - Preferences Order has changed");
	            clearPrimary();
			}
		}
		defaultData.serverAddress = prefService.getString(VLConnectionPlugin.PLUGIN_ID, ConnectionPreferencePage.P_SERVER, "", null);
		currentData.serverAddress = defaultData.serverAddress;
		defaultData.port = MPiece.getPiece(prefService.getString(VLConnectionPlugin.PLUGIN_ID, ConnectionPreferencePage.P_PORT, "", null),";");
		currentData.port = defaultData.port;
		defaultData.serverProject = prefService.getString(VLConnectionPlugin.PLUGIN_ID, ConnectionPreferencePage.P_PORT, "", null);
		defaultData.serverProject = MPiece.getPiece(defaultData.serverProject,";",2);
		currentData.serverProject = defaultData.serverProject;
		setPrimaryServer();
	}
	
	static public boolean getPrimaryServer() {
		setBadConnection(false);
        String str = primaryData.toString();
        try {
        	getDefaultPrefs();
        } catch (Exception e) {
        	defaultData.serverName = "";
        	setBadConnection(true);  //  110817
        	return false;  //  110817
        }
        String defaultStr = defaultData.toString();
        if (str.compareTo(";;;") == 0) {
        	str = defaultStr;
        }
        if (currentData.serverName.compareTo("") == 0) {
          	currentData = new ServerConnectionData(defaultData);
        }
        if (str.compareTo(";;;") == 0) { // JLI 090908 added for Source Code Version Control") == 0) {
        	primaryData = new ServerConnectionData(defaultData);
        	currentData = new ServerConnectionData(defaultData);
 
        	currConnection = null;
            primaryConnection = null;
            userPrincipal = null;
            eclipseConnection = null;
            if (! isBadConnection()) {
            	VistaConnection.getConnection();
            }
 			return !isBadConnection();
        }
        return !isBadConnection();
    }
	
    static public String getPrimaryProject() {
    	return primaryData.serverProject;
    }
    
    static public String getPrimaryServerName() {
    	return primaryData.serverName;
    }
	
	static public void setServer(ConnectionData connData) {
		currConnection = connData.getConnection();
		eclipseConnection = connData.getEclipseConnection();
		userPrincipal = connData.getPrincipal();
		currentData.serverAddress = connData.getServerAddress();
		currentData.port = connData.getServerPort();
		currentData.serverName = connData.getServerName();
	}
	
	@SuppressWarnings("unchecked")
	static public void setPrimaryServer() {
		primaryConnection = currConnection;
		primaryPrincipal = userPrincipal;
		primaryData = new ServerConnectionData(currentData);
        ConnectionData connData = getMatchingConnection(currentData.port, currentData.serverAddress); // JLI 100226 changed for change in method, since connection depends only on port and url 
		if (connData == null) {
				currConnection = getConnection(currentData);
			connData = getMatchingConnection(currentData.port, currentData.serverAddress); // JLI 100226 changed for change in method, since connection depends only on port and url
			if ((connData == null) && (currConnection != null)) {
				connData = new ConnectionData();
				connData.setServerName(currentData.serverName);
				connData.setServerPort(currentData.port);
				connData.setConnection(currConnection);
				connData.setPrincipal(userPrincipal);
				connData.setEclipseConnection(eclipseConnection);
				connectionList.add(connData);
			}
		}
		if (connData != null) {
			setServer(connData);
		}
	}
	
	static public void clearPrimary(){
			primaryData.reset();
			primaryConnection = null;
			primaryPrincipal = null;
			if (defaultData.serverName.compareTo("") != 0) {
				primaryData = new ServerConnectionData(defaultData);
				currentData = new ServerConnectionData(defaultData);
			}
	}
	
	static public String getCurrentServer() {
		return currentData.toString();
    }
	
   static public String getCurrentProject() {
        return currentData.serverProject;
	}
	
	static public VistaLinkConnection getCurrentConnection() {
		if (currConnection == null) {  // must be primary connection
			currConnection = VistaConnection.getConnection();
			if (currConnection == null) {
				return null;
			}
		}
		currConnection = getMatchingConnection(currentData.port, currentData.serverAddress).getConnection();
		if (currConnection == null) {
			currConnection = getConnection(currentData);
		}
		if (currConnection != null) {
			if (! checkConnection(currConnection, primaryData.port, primaryData.serverAddress)) {
				currConnection = null;
				currConnection = getCurrentConnection();
			}
		}
		return currConnection;
	}

	public static void doDispose() {
		if (! (currConnection == null)) {
			disconnect();
		}
	}

	static public ConnectionData getMatchingConnection(String portValue, String serverURL) { // JLI 090908 added for Source Code Version Control) {
		for (int i=0; i<connectionList.size(); i++) {
			if (! (connectionList.get(i) == null) ) {
				ConnectionData connData = (ConnectionData)connectionList.get(i);
				@SuppressWarnings("unused")
				String serverPort = connData.getServerPort();
				@SuppressWarnings("unused")
				String serverAddress = connData.getServerAddress();
				if (connData.getServerPort().equalsIgnoreCase(portValue)
						&& connData.getServerAddress().equalsIgnoreCase(serverURL)){ // JLI 090908 added for Source Code Version Control
					return connData;
				}
			}
		}
		return null;
	}
	
	/*
	 * checkForProductionAccount - determines whether the current connection is to a PRODUCTION account or not
	 * 
	 * argument myConnection - VistaLinkConnection - the current connection
	 * 
	 * returns boolean indicating whether the current connection is indicated as a PRODUCTION system (true) or not (false).
	 */
	static public boolean checkForProductionAccount(VistaLinkConnection myConnection) throws Exception {
		RpcRequest vReq1 = RpcRequestFactory.getRpcRequest("","XT ECLIPSE M EDITOR");
		vReq1.setUseProprietaryMessageFormat(true);
		vReq1.getParams().setParam(1, "string", "PROD");  // RD  RL  GD  GL  RS PROD
		RpcResponse vResp1 = myConnection.executeRPC(vReq1);
		boolean response = false;
		if (vResp1.getResults().indexOf("1") == 0) {
			response = true;
		}
		return response;
	}

	static void ShowConnectionServerMsg() throws Exception {
		if (! shownNoServerDefined ) {
			shownNoServerDefined = true;
			Shell parentShell = Display.getDefault().getActiveShell();
			MessageDialog.openInformation(
					parentShell,
					"Server, Port Specification", 
			"Open Windows | Preferences | Vista | Connection and enter Server and Port data");
		}
		throw new Exception("No valid Server currently defined - Open Windows | Preferences | Vista | Connection and enter Server and Port data");
	}
	
	static public void clearShownNoServerDefined() {
		shownNoServerDefined = false;
	}
	
	static public void setBadConnection(boolean state) {
		badConnection = state;
	}
	
	static public boolean isBadConnection() {
		return badConnection;
	}
}
