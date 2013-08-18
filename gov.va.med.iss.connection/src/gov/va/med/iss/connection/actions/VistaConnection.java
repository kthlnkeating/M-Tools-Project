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
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * Our sample action implements workbench action delegate.
 * The action proxy will be created by the workbench and
 * shown in the UI. When the user tries to use the action,
 * this delegate will be created and execution will be 
 * delegated to it.
 * @see IWorkbenchWindowActionDelegate
 */
public class VistaConnection {
	/**
	 * The constructor.
	 */
	
	private static EclipseConnection eclipseConnection = null;
	private static VistaKernelPrincipalImpl userPrincipal = null;
	private static VistaKernelPrincipalImpl primaryPrincipal = null;
	
	private static List<ConnectionData> connectionList = new ArrayList<ConnectionData>();
	
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
							"server is a\n"+
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
		        if (! checkConnection(primaryConnection, primaryData)) {
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
	private static boolean checkConnection(VistaLinkConnection connection, ServerConnectionData serverData) {
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
				connection.executeRPC(vReq);
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
            ConnectionData connData = getMatchingConnection(serverData);
			removeConnection(connData);
		}
		return result;
	}
	
	public static VistaLinkConnection getConnection(ServerConnectionData data) {
		currConnection = null;
		ConnectionData matchingConnData = getMatchingConnection(data);
		if (matchingConnData != null) {
			setServer(matchingConnData);
			if (! checkConnection(currConnection, data)) {
				currConnection = null;
			}
			if (currConnection != null)
				return currConnection;
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
					String name = MPiece.getPiece(servers[i],";",2);
					String port = MPiece.getPiece(servers[i],";",4);
					String url = MPiece.getPiece(servers[i],";",3);
					String project = MPiece.getPiece(servers[i],";",5);
                    ServerConnectionData data = new ServerConnectionData(url, name, port, project);                    
                    ConnectionData connData = getMatchingConnection(data);
					if (! (connData == null)) {
						totCount++;
					}
				}
				String[] activeServers = new String[totCount];
				totCount = 0;
				for (int i=0; i<servers.length; i++) {
					String name = MPiece.getPiece(servers[i],";",2);
					String port = MPiece.getPiece(servers[i],";",4);
					String url = MPiece.getPiece(servers[i],";",3);
					String project = MPiece.getPiece(servers[i],";",5); // JLI 090908 added for Source Code Version Control
                    ServerConnectionData data = new ServerConnectionData(url, name, port, project);                    
                    ConnectionData connData = getMatchingConnection(data); // JLI 100226
					if (! (connData == null)) {
						activeServers[totCount++] = servers[i];
					}
				}
				SecondaryServerSelectionDialogForm secondary = new SecondaryServerSelectionDialogForm(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),SWT.NONE);
				SecondaryServerSelectionDialogData data = secondary.open("DISCONNECT",activeServers);
				checkedServers = data.getCheckedList();
				for (int i=0; i<checkedServers.length; i++) {
					String name = MPiece.getPiece(checkedServers[i],";",1);
					String port = MPiece.getPiece(checkedServers[i],";",3);
					String url = MPiece.getPiece(checkedServers[i],";",2);
					String project = MPiece.getPiece(checkedServers[i],";",4); // JLI 090908 added for Source Code Version Control
                    ServerConnectionData data2 = new ServerConnectionData(url, name, port, project);                    
                    ConnectionData connData = getMatchingConnection(data2); // JLI 110914
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
		if (! (currConnection == null)) {
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
		if ((currentData.serverAddress.compareTo("") != 0) && (getMatchingConnection(currentData) == null)){
//				PropertyConfigurator.configure("log4jConfig.properties");
//				DOMConfigurator.configure("%ECLIPSEHOME%/plugins/gov.va.med.iss.connection_0.9.0/log4jConfig.xml");
			BasicConfigurator.configure();
//				logger.fatal("output from logger.fatal");
//				System.out.println("Output from println in VistaConnection");
		
			try {
				eclipseConnection = new EclipseConnection();
			} catch (Exception e) {
				eclipseConnection = null;
				IStatus status = new Status(IStatus.ERROR, VLConnectionPlugin.PLUGIN_ID, "VistA connection error in EclipseConnection creation.", e);
				StatusManager.getManager().handle(status, StatusManager.SHOW);
			}
			if (!(eclipseConnection == null)) {
				try {  // The following line gets the connection to the server
					userPrincipal = eclipseConnection.getConnection(currentData.serverAddress, currentData.port);
				} catch (Exception e) {
					IStatus status = new Status(IStatus.ERROR, VLConnectionPlugin.PLUGIN_ID, "VistA connection error in eclipseConnection.getConnection.", e);
					StatusManager.getManager().handle(status, StatusManager.SHOW);
					userPrincipal = null;
				}
				if (!(userPrincipal == null)) {
					try {
						currConnection = userPrincipal.getAuthenticatedConnection();
					} catch (Exception e) {
						IStatus status = new Status(IStatus.ERROR, VLConnectionPlugin.PLUGIN_ID, "VistA connection error in userPrincipal.getAuthenicatedConnection().", e);
						StatusManager.getManager().handle(status, StatusManager.SHOW);
					}
				}
			}
		}
	}
	
	static public ServerConnectionData getDefaultConnectionData() {
		IPreferencesService prefService = Platform.getPreferencesService();

		String serverName = prefService.getString(VLConnectionPlugin.PLUGIN_ID, ConnectionPreferencePage.P_SERVER_NAME, "", null);
		String serverAddress = prefService.getString(VLConnectionPlugin.PLUGIN_ID, ConnectionPreferencePage.P_SERVER, "", null);
		String port = MPiece.getPiece(prefService.getString(VLConnectionPlugin.PLUGIN_ID, ConnectionPreferencePage.P_PORT, "", null),";");
		String serverProject = MPiece.getPiece(prefService.getString(VLConnectionPlugin.PLUGIN_ID, ConnectionPreferencePage.P_PORT, "", null),";",2);

		return new ServerConnectionData(serverAddress, serverName, port, serverProject);
	}
	
	
	static public void getDefaultPrefs() throws Exception {
		defaultData = getDefaultConnectionData();
		if (defaultData.serverName == "") {
			ShowConnectionServerMsg();
		}
		if (primaryData.serverName.compareTo("") != 0) {
			if (primaryData.serverName.compareTo(defaultData.serverName) != 0) {
	            IWorkbench wb = PlatformUI.getWorkbench();
	            IWorkbenchWindow window = wb.getActiveWorkbenchWindow();
	            MessageDialog.openInformation(
	                    window.getShell(),
	                    "VistA Connection Change",
	                    "Switching Primary Servers - Preferences Order has changed");
	            clearPrimary();
			}
		}
		currentData = new ServerConnectionData(defaultData);
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
	
	static public void setPrimaryServer() {
		primaryConnection = currConnection;
		primaryPrincipal = userPrincipal;
		primaryData = new ServerConnectionData(currentData);
        ConnectionData connData = getMatchingConnection(currentData); 
		if (connData == null) {
				currConnection = getConnection(currentData);
			connData = getMatchingConnection(currentData);
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
	
	public static void doDispose() {
		if (! (currConnection == null)) {
			disconnect();
		}
	}

	static public ConnectionData getMatchingConnection(ServerConnectionData serverData) {
		for (ConnectionData connData : connectionList) {
			if (connData != null) {
				if (connData.matches(serverData)) {
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
